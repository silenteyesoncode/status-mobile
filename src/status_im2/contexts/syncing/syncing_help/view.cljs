(ns status-im2.contexts.syncing.syncing-help.view
  (:require [quo2.core :as quo]
            [quo2.foundations.colors :as colors]
            [react-native.core :as rn]
            [react-native.gesture :as gesture]
            [reagent.core :as reagent]
            [status-im2.common.resources :as resources]
            [status-im2.contexts.syncing.syncing-help.style :as style]
            [utils.i18n :as i18n]
            [utils.re-frame :as rf]))

(defn- render-element
  [[type value]]
  (case type
    :text
    [quo/text
     {:size   :paragraph-2
      :weight :regular
      :style  style/list-text} (i18n/label value)]

    :button-primary
    [quo/button
     {:type  :primary
      :size  24
      :style style/button-primary} (i18n/label value)]

    :button-grey
    [quo/button
     {:type           :grey
      :override-theme :dark
      :size           24
      :style          style/button-grey}
     (i18n/label value)]

    :button-grey-placeholder
    [quo/button
     {:type           :grey
      :override-theme :dark
      :size           24
      :before         :i/placeholder
      :style          style/button-grey-placeholder}
     (i18n/label value)]

    :context-tag
    [quo/context-tag
     {:override-theme :dark
      :text-style     {:color colors/white}}
     (resources/get-mock-image (:source value))
     (i18n/label (:label value))]))

(defn- render-item
  [i list-item]
  ^{:key i}
  [rn/view
   {:margin-vertical 6
    :style           style/numbered-list-item}
   [rn/view
    {:margin-right 6
     :style        style/list-icon}
    [quo/text
     {:size   :label
      :weight :medium
      :style  style/list-icon-text} i]]
   (map-indexed (fn [idx item]
                  ^{:key idx}
                  [render-element item])
                list-item)])

(defn- render-list
  [{:keys [title image list]}]
  [rn/view
   (when title
     [quo/text
      {:size   :paragraph-1
       :weight :semi-bold
       :style  style/paragraph} (i18n/label title)])
   (case (:type image)
     :container-image [rn/view {:style style/container-image}
                       [rn/image
                        {:source (resources/get-image
                                  (:source image))}]]
     :image           [rn/image
                       {:source (resources/get-image (:source image))
                        :style  style/image}]
     nil)
   [rn/view {:style style/numbered-list}
    (map-indexed (fn [i item] (render-item (inc i) item)) list)]])

(def how-to-pair-data
  {:mobile
   [gesture/scroll-view
    (render-list {:title :t/signing-in-from-another-device
                  :image {:source :mobile-how-to-pair-sign-in
                          :type   :image}
                  :list  [[[:text :t/open-status-on-your-other-device]]
                          [[:text :t/tap-on]
                           [:button-grey :t/im-new]]
                          [[:button-primary :t/enable-camera]
                           [:text :t/or-tap]
                           [:button-grey :t/enter-sync-code]]
                          [[:text :t/scan-or-enter-sync-code-seen-on-this-device]]]})
    [rn/view {:style style/hr}]
    (render-list {:title :t/already-logged-in-on-the-other-device
                  :image {:source :mobile-how-to-pair-logged-in
                          :type   :image}
                  :list  [[[:text :t/tap-on]
                           [:context-tag
                            {:label  :t/profile
                             :source :user-picture-male5}]
                           [:text :t/and]
                           [:button-grey-placeholder :t/syncing]]
                          [[:text :t/press]
                           [:button-grey :t/scan-or-enter-sync-code]]
                          [[:button-primary :t/enable-camera]
                           [:text :t/or-tap]
                           [:button-grey :t/enter-sync-code]]
                          [[:text :t/scan-or-enter-sync-code-seen-on-this-device]]]})]
   :desktop
   [gesture/scroll-view
    (render-list {:title :t/signing-in-from-another-device
                  :image {:source :desktop-how-to-pair-sign-in
                          :type   :image}
                  :list  [[[:text :t/open-status-on-your-other-device]]
                          [[:text :t/open]
                           [:button-grey :t/settings]
                           [:text :t/and-go-to]
                           [:button-grey :t/syncing]]
                          [[:text :t/tap]
                           [:button-grey :t/scan-or-enter-a-sync-code]]
                          [[:text :t/scan-the-qr-code-or-copy-the-sync-code]]]})
    [rn/view {:style style/hr}]
    (render-list {:title :t/already-logged-in-on-the-other-device
                  :image {:source :desktop-how-to-pair-logged-in
                          :type   :container-image}
                  :list  [[[:text :t/open-status-on-your-other-device]]
                          [[:text :t/tap-on]
                           [:button-grey :t/im-new]]
                          [[:button-primary :t/enable-camera]
                           [:text :t/or-tap]
                           [:button-grey :t/enter-sync-code]]
                          [[:text :t/scan-or-enter-sync-code-seen-on-this-device]]]})]})

(def find-sync-code-data
  {:mobile
   [gesture/scroll-view
    (render-list {:image {:source :find-sync-code-mobile
                          :type   :image}
                  :list  [[[:text :t/open-status-on-your-other-device]]
                          [[:text :t/open-your]
                           [:context-tag
                            {:label  :t/profile
                             :source :user-picture-male5}]]
                          [[:text :t/go-to]
                           [:button-grey-placeholder :t/syncing]]
                          [[:text :t/tap]
                           [:button-grey :t/sync-new-device]
                           [:text :t/and]
                           [:button-primary :t/set-up-sync]]
                          [[:text :t/scan-the-qr-code-or-copy-the-sync-code]]]})]
   :desktop
   [gesture/scroll-view
    (render-list {:image {:source :find-sync-code-desktop
                          :type   :image}
                  :list  [[[:text :t/open-status-on-your-other-device]]
                          [[:text :t/open]
                           [:button-grey-placeholder :t/settings]
                           [:text :t/and-go-to]
                           [:button-grey-placeholder :t/syncing]]
                          [[:text :t/tap]
                           [:button-primary :t/set-up-sync]]
                          [[:text :t/scan-the-qr-code-or-copy-the-sync-code]]]})]})

(defn- get-display-data
  [type]
  (case type
    :how-to-pair
    {:header-label-key :t/how-to-pair
     :data             how-to-pair-data}

    :find-sync-code
    {:header-label-key :t/find-sync-code
     :data             find-sync-code-data}

    {:header-label-key :t/how-to-pair
     :data             how-to-pair-data}))

(defn instructions
  []
  (let [{:keys [type]}                  (rf/sub [:get-screen-params])
        platform                        (reagent/atom :mobile)
        platform-data                   [{:id    :mobile
                                          :label (i18n/label :t/mobile)}
                                         {:id    :desktop
                                          :label (i18n/label :t/desktop)}]
        {:keys [header-label-key data]} (get-display-data type)]
    (fn []
      [rn/view {:style style/container-outer}
       [quo/text
        {:size   :heading-1
         :weight :semi-bold
         :style  style/heading} (i18n/label header-label-key)]
       [rn/view {:style style/tabs-container}
        [quo/segmented-control
         {:size           28
          :override-theme :dark
          :blur?          true
          :default-active :mobile
          :data           platform-data
          :on-change      #(reset! platform %)}]]
       (@platform data)])))

