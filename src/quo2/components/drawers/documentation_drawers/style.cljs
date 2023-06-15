(ns quo2.components.drawers.documentation-drawers.style
  (:require [react-native.platform :as platform]
            [react-native.safe-area :as safe-area]))

(def container
  {:align-items        :flex-start
   :background-color   :red
   :padding-horizontal 20
   })

(def content
  {:margin-top       8
   :background-color :green
   :margin-bottom    (+ (safe-area/get-bottom) 8)
   })
