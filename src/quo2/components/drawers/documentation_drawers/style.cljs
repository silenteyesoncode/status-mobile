(ns quo2.components.drawers.documentation-drawers.style
  (:require [react-native.safe-area :as safe-area]))

(def container
  {:align-items        :flex-start
   :padding-horizontal 20})

(def content
  {:margin-top    8
   :margin-bottom (+ (safe-area/get-bottom) 8)})

;; TODO: test on iPhone SE
