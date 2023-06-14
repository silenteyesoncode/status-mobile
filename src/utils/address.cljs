(ns utils.address
  ;; TODO move to status-im2
  (:require [status-im.ethereum.core :as ethereum]
            [status-im.ethereum.eip55 :as eip55]))

(defn get-shortened-key
  "Takes first and last 4 digits from address including leading 0x
  and adds unicode ellipsis in between"
  [value]
  (when value
    (str (subs value 0 6) "\u2026" (subs value (- (count value) 3) (count value)))))

(defn get-shortened-checksum-address
  [address]
  (when address
    (get-shortened-key (eip55/address->checksum (ethereum/normalized-hex address)))))

(defn get-abbreviated-profile-url
  "The goal here is to generate a string that begins with
   join.status.im/u/ joined with the 1st 5 characters
   of the compressed public key followed by an ellipsis followed by
   the last 12 characters of the compressed public key"
  [base-url public-pk]
  (let [first-part-of-public-pk (subs public-pk 0 5)
        ellipsis                "..."
        public-pk-size          (count public-pk)
        last-part-of-public-pk  (subs public-pk (- public-pk-size 12) (- public-pk-size 1))
        abbreviated-url         (str base-url first-part-of-public-pk ellipsis last-part-of-public-pk)]
    abbreviated-url))
