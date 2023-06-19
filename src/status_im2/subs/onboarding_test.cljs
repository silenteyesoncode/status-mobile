(ns status-im2.subs.onboarding-test
  (:require [cljs.test :as t]
            [quo2.theme :as theme]
            [re-frame.db :as rf-db]
            status-im2.subs.onboarding
            [test-helpers.unit :as h]
            [utils.image-server :as image-server]
            [utils.re-frame :as rf]))

(def key-uid "0x1")
(def cur-theme :current-theme)

(h/deftest-sub :multiaccounts/login-profiles-picture
  [sub-name]
  (with-redefs [image-server/get-account-image-uri identity
                theme/get-theme                    (constantly cur-theme)]
    (t/testing "nil when no key-uid"
      (swap! rf-db/app-db assoc :multiaccounts/multiaccounts {key-uid {}})
      (t/is (nil? (rf/sub [sub-name "0x2"]))))

    (t/testing "initials fn when no images"
      (swap! rf-db/app-db assoc :multiaccounts/multiaccounts {key-uid {}})
      (t/is (:fn (rf/sub [sub-name key-uid]))))

    (t/testing "account fn when custom image set"
      (swap! rf-db/app-db assoc :multiaccounts/multiaccounts {key-uid {}})
      (t/is (:fn (rf/sub [sub-name key-uid]))))))
