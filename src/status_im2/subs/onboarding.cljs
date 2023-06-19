(ns status-im2.subs.onboarding
  (:require [quo2.theme :as theme]
            [re-frame.core :as re-frame]
            [status-im.multiaccounts.recover.core :as recover]
            [status-im2.constants :as constants]
            [utils.image-server :as image-server]))

(re-frame/reg-sub
 :intro-wizard
 :<- [:intro-wizard-state]
 :<- [:dimensions/window]
 (fn [[wizard-state {:keys [width height]}]]
   (assoc wizard-state :view-height height :view-width width)))

(re-frame/reg-sub
 :intro-wizard/choose-key
 :<- [:intro-wizard]
 (fn [wizard-state]
   (select-keys wizard-state [:multiaccounts :selected-id :view-height])))

(re-frame/reg-sub
 :intro-wizard/select-key-storage
 :<- [:intro-wizard]
 (fn [wizard-state]
   (select-keys wizard-state [:selected-storage-type :recovering?])))

(re-frame/reg-sub
 :intro-wizard/enter-phrase
 :<- [:intro-wizard]
 (fn [wizard-state]
   (select-keys wizard-state
                [:processing?
                 :passphrase-word-count
                 :next-button-disabled?
                 :passphrase-error])))

(re-frame/reg-sub
 :intro-wizard/recovery-success
 :<- [:intro-wizard]
 (fn [wizard-state]
   {:pubkey         (get-in wizard-state [:derived constants/path-whisper-keyword :public-key])
    :compressed-key (get-in wizard-state [:derived constants/path-whisper-keyword :compressed-key])
    :name           (get-in wizard-state [:derived constants/path-whisper-keyword :name])
    :processing?    (:processing? wizard-state)}))

(re-frame/reg-sub
 :intro-wizard/recover-existing-account?
 :<- [:intro-wizard]
 :<- [:multiaccounts/multiaccounts]
 (fn [[intro-wizard multiaccounts]]
   (recover/existing-account? (:root-key intro-wizard) multiaccounts)))

(re-frame/reg-sub
 :intro-wizard/placeholder-avatar
 :<- [:mediaserver/port]
 :<- [:initials-avatar-font-file]
 (fn [[port font-file] [_ profile-pic]]
   {:fn
    (if profile-pic
      (image-server/get-account-image-uri-fn {:port           port
                                              :image-name     profile-pic
                                              :override-ring? false
                                              :theme          (theme/get-theme)})
      (image-server/get-initials-avatar-uri-fn {:port           port
                                                :theme          (theme/get-theme)
                                                :override-ring? false
                                                :font-file      font-file}))}))

(re-frame/reg-sub
 :multiaccounts/login-profiles-picture
 :<- [:multiaccounts/multiaccounts]
 :<- [:mediaserver/port]
 :<- [:initials-avatar-font-file]
 (fn [[multiaccounts port font-file] [_ target-key-uid]]
   (let [{:keys [images ens-name?] :as multiaccount} (get multiaccounts target-key-uid)
         image-name                                  (-> images first :type)
         override-ring?                              (not ens-name?)]
     (when multiaccount
       {:fn
        (if image-name
          (image-server/get-account-image-uri-fn {:port           port
                                                  :image-name     image-name
                                                  :key-uid        target-key-uid
                                                  :theme          (theme/get-theme)
                                                  :override-ring? override-ring?})
          (image-server/get-initials-avatar-uri-fn {:port           port
                                                    :key-uid        target-key-uid
                                                    :theme          (theme/get-theme)
                                                    :override-ring? override-ring?
                                                    :font-file      font-file}))}))))

(defn login-ma-keycard-pairing
  "Compute the keycard-pairing value of the multiaccount selected for login"
  [db _]
  (when-let [acc-to-login (-> db :multiaccounts/login)]
    (-> db
        :multiaccounts/multiaccounts
        (get (:key-uid acc-to-login))
        :keycard-pairing)))

(re-frame/reg-sub
 :intro-wizard/acc-to-login-keycard-pairing
 login-ma-keycard-pairing)
