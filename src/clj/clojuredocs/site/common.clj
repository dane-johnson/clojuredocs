(ns clojuredocs.site.common
  (:require [clojuredocs.util :as util]
            [clojuredocs.config :as config]
            [clojuredocs.env :as env]
            [clojuredocs.github :as gh]))

(def gh-auth-url (gh/auth-redirect-url
                   (merge config/gh-creds
                          {:redirect-uri (config/url "/gh-callback")})))

(defn $user-area [user]
  [:li.user-area
   [:img.avatar {:src (:avatar-url user)}]
   " | "
   [:a {:href "/logout"}
    "Log Out"]])

(defn $navbar [{:keys [user hide-search]}]
  [:header.navbar
   [:div.container
    [:div.row
     [:div.col-md-10.col-md-offset-1
      [:a.navbar-brand {:href "/"}
       [:i.icon-rocket]
       "ClojureDocs"]
      [:ul.navbar-nav.nav.navbar-right
       [:li [:a {:href "/"} "Home"]]
       [:li [:a {:href "/quickref"} "Quick Reference"]]
       (if user
         ($user-area user)
         [:li [:a {:href gh-auth-url} "Log In"]])]
      (when-not hide-search
        [:form.search.navbar-form.navbar-right
         {:method :get :action "/search" :autocomplete "off"}
         [:input.form-control {:type "text"
                               :name "query"
                               :placeholder "Quick Lookup (ctrl-s)"
                               :autocomplete "off"}]])]]
    (when-not hide-search
      [:div.row
       [:div.col-md-10.col-md-offset-1
        [:table.ac-results]]])]])

(defn $main [{:keys [content title body-class user page-data] :as opts}]
  [:html5
   [:head
    [:meta {:name "viewport" :content "width=device-width, maximum-scale=1.0"}]
    [:title (or title "Community-Powered Clojure Documentation and Examples | ClojureDocs")]
    [:link {:rel :stylesheet :href "/css/bootstrap.min.css"}]
    [:link {:rel :stylesheet :href "/css/font-awesome.min.css"}]
    [:link {:rel :stylesheet :href "/css/app.css"}]
    [:link {:rel :stylesheet :href "//fonts.googleapis.com/css?family=Open+Sans:400" :type "text/css"}]
    [:script "window.PAGE_DATA=" (util/to-json (pr-str page-data)) ";"]]
   [:body
    [:div.sticky-wrapper
     (when body-class
       {:class body-class})
     ($navbar opts)
     [:div.container
      [:div.row
       [:div.col-md-10.col-md-offset-1
        content]]]
     [:div.sticky-push]]
    [:footer
     [:div.divider
      "⤜ ❦ ⤛"]
     [:div.ctas
      "Brought to you by "
      [:a {:href "https://twitter.com/heyzk"} "@heyzk"]
      ". "
      "&nbsp; / "
      [:iframe {:src "http://ghbtns.com/github-btn.html?user=zk&repo=clojuredocs&type=watch&count=true"
                :allowtransparency "true"
                :frameborder "0"
                :scrolling "0"
                :width "80"
                :height "20"}]
      [:iframe {:src "http://ghbtns.com/github-btn.html?user=zk&repo=clojuredocs&type=fork&count=true"
                :allowtransparency "true"
                :frameborder "0"
                :scrolling "0"
                :width "80"
                :height "20"}]
      [:a.twitter-share-button {:href "https://twitter.com/share"
                                :data-url "https://www.clojuredocs.org"
                                :data-text "Community-powered docs and examples for #Clojure"
                                :data-via "heyzk"}
       "Tweet"]
      [:script
       "!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document, 'script', 'twitter-wjs');"]]]
    (when (env/bool :cljs-dev)
      [:script {:src "http://fb.me/react-0.9.0.js"}])
    (when (env/bool :cljs-dev)
      [:script {:type "text/javascript" :src "/cljs/goog/base.js"}])
    [:script {:type "text/javascript" :src "/cljs/clojuredocs.js"}]
    (when (env/bool :cljs-dev)
      [:script {:type "text/javascript"} "goog.require(\"clojuredocs.main\");"])]])

(defn $avatar [{:keys [email login] :as user}]
  [:a {:href (str "/u/" login)}
   [:img.avatar {:src (str "https://www.gravatar.com/avatar/" (util/md5 email) "?r=PG&s=32&default=identicon") }]])
