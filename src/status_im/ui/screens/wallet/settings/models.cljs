(ns status-im.ui.screens.wallet.settings.models
  (:require [re-frame.core :as re-frame]
            [status-im.accounts.update.core :as accounts.update]
            [status-im.ui.screens.wallet.events :as wallet.events]
            [status-im.utils.ethereum.core :as ethereum]
            [status-im.utils.ethereum.tokens :as tokens]
            [status-im.utils.fx :as fx]))

(defn- set-checked [ids id checked?]
  (if checked?
    (conj (or ids #{}) id)
    (disj ids id)))

(defn update-toggle-in-settings [{{:account/keys [account]} :db} symbol checked?]
  (let [network      (get (:networks account) (:network account))
        chain        (ethereum/network->chain-keyword network)
        settings     (get account :settings)]
    (update-in settings [:wallet :visible-tokens chain] #(set-checked % symbol checked?))))

(fx/defn toggle-visible-token [cofx symbol checked?]
  (let [new-settings (update-toggle-in-settings cofx symbol checked?)]
    (accounts.update/update-settings cofx new-settings {})))

(fx/defn add-custom-token [{{:account/keys [account]} :db :as cofx} {:keys [symbol address] :as token}]
  (let [network      (get (:networks account) (:network account))
        chain        (ethereum/network->chain-keyword network)
        settings     (update-toggle-in-settings cofx symbol true)
        new-settings (assoc-in settings [:wallet :custom-tokens chain address] token)]
    (accounts.update/update-settings cofx new-settings {})))

(fx/defn remove-custom-token [{{:account/keys [account]} :db :as cofx} {:keys [symbol address]}]
  (let [network      (get (:networks account) (:network account))
        chain        (ethereum/network->chain-keyword network)
        settings     (update-toggle-in-settings cofx symbol false)
        new-settings (update-in settings [:wallet :custom-tokens chain] dissoc address)]
    (accounts.update/update-settings cofx new-settings {})))

(fx/defn configure-token-balance-and-visibility [cofx symbol balance]
  (fx/merge cofx
            (toggle-visible-token symbol true)
                  ;;TODO(goranjovic): move `update-token-balance-success` function to wallet models
            (wallet.events/update-token-balance-success symbol balance)))

(fx/defn wallet-autoconfig-tokens [{:keys [db]}]
  (let [{:keys [account/account web3 network-status] :wallet/keys [all-tokens]} db
        network   (get (:networks account) (:network account))
        chain     (ethereum/network->chain-keyword network)
        contracts (->> (tokens/tokens-for all-tokens chain)
                       (remove :hidden?))]
    (when-not (= network-status :offline)
      (doseq [{:keys [address symbol]} contracts]
        ;;TODO(goranjovic): move `get-token-balance` function to wallet models
        (wallet.events/get-token-balance {:web3       web3
                                          :contract   address
                                          :account-id (:address account)
                                          :on-error   #(re-frame/dispatch [:update-token-balance-fail symbol %])
                                          :on-success #(when (> % 0)
                                                         (re-frame/dispatch [:configure-token-balance-and-visibility symbol %]))})))))
