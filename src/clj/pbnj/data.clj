(ns pbnj.data
  (:refer-clojure :exclude [send defmethod]))

(set! *warn-on-reflection* true)

(defprotocol Obj
  (find-method [this msg]
    "Return the method that corresponds to the message or nil"))

(def send-message nil)

(deftype Head
  [id name version methods attributes]
  Obj
  (find-method [this msg]
    (get @(.methods this) msg))

  clojure.lang.Named
  (getNamespace [_] nil)
  (getName [this]
    @(.name this))

  Object
  (toString [this]
    (if-let [name (.getName this)]
      (str "#<Head " name " v" version ">")
      (str "#<Head " id " v" version ">")))

  (equals [this other]
    (= (.id this) (.id other)))

  clojure.lang.IFn
  (applyTo [this args] (apply send-message this args))
  (invoke [this msg] (send-message this msg)))

(defn head
  [& {:keys [id name version methods attributes]
      :or {id (random-uuid)
           name nil
           version 0
           methods (atom {} :validator map?)
           attributes (atom {} :validator map?)}}]
  (->Head
   id
   (atom name :validator #(or (nil? %) (string? %)))
   version
   methods
   attributes))

(defn set-name! [^Head obj name]
  (reset! (.name obj) name)
  obj)

(defn head?
  [obj]
  (instance? Head obj))

(defn method? [obj msg]
  (fn? (find-method obj msg)))

(defn message-name [msg]
  (cond (keyword? msg) msg
        (symbol? msg) (keyword (name msg))
        (vector? msg) (msg 0)
        (list? msg) (let [tag (first msg)]
                      (if (symbol? tag)
                        (keyword (name tag))
                        tag))
        :else (throw (ex-info (str "Invalid message: " (pr-str msg)) {:msg msg}))))

(defn message-args [msg]
  (cond (keyword? msg) '()
        (symbol? msg) '()
        (vector? msg) (subvec msg 1)
        (list? msg) (rest msg)
        :else (throw (ex-info (str "Invalid message: " (pr-str msg)) {:msg msg}))))

(defn send-message [obj msg]
  (let [name   (message-name msg)
        args   (message-args msg)
        method (find-method obj name)]
    (if (and method (fn? method))
      (apply method obj args)
      (if (method? obj :doesnt-understand)
        (send-message obj [:doesnt-understand msg args])
        (throw (ex-info (str "Method " msg " not found") {:obj obj :msg msg}))))))


(defmacro send [obj msg]
  `(send-message ~obj (quote ~msg)))

(defn clone [^Head obj]
  (head :version (.version obj) :name @(.name obj)
        :methods (atom @(.methods obj) :validator map?)
        :attributes (atom @(.attributes obj) :validator map?)))

(defn add-method! [^Head node msg f]
  (swap! (.methods node) assoc msg f)
  node)

(defmacro defmethod [obj msg binds & body]
  (let [name (symbol (name msg))]
    `(add-method! ~obj ~msg (fn ~name ~binds ~@body))))

(comment
  (macroexpand '(method greet [name] (str "Hi " name)))
  (macroexpand '(defmethod root :greet [self name] (str "Hi " name)))
  )

(comment
  (find-method (head :methods (atom {:hi (fn [& _] "Hi")})) :hi)
  (method? (head :methods (atom {:hi (fn [& _] "Hi")})) :hi)
  (send (head :methods (atom {:hi (fn [& _] "Hi")})) :hi)
  (find-method (head :methods (atom {:hi (fn [& _] "Hi")})) :bye)
  (method? (head :methods (atom {:hi (fn [& _] "Hi")})) :bye)
  (send (head :methods (atom {:hi (fn [& _] "Hi")})) :bye)
  (.invoke (head :methods (atom {:hi (fn [& _] "Hi")})) :hi)
  (.invoke (head :methods (atom {:echo (fn [& args] args)})) :echo '(1 2 3))
  (:echo (head (atom {:echo (fn [& args] args)})) (+ 4 5))
  (set-name! (head :methods (atom {:hi (fn [& _] "Hi")})) "greeter")
  )

(def basis (head))

(add-method! basis :version
  (fn version [^Head this] (.version this)))

(add-method! basis :attributes
  (fn attributes [^Head this] @(.attributes this)))

(add-method! basis :get
  (fn attributes [^Head this name] (get @(.attributes this) name)))

(defmethod basis :set!
  [^Head this key value]
  (swap! (.attributes this) assoc key value)
  this)

(add-method! basis :clone clone)
(add-method! basis :send send-message)
(add-method! basis :understands? method?)

(add-method! basis :messages
  (fn messages [^Head this] (keys @(.methods this))))

(add-method! basis :id
  (fn name [^Head this] (.id this)))

(add-method! basis :name
  (fn name [^Head this] @(.name this)))

(defmethod basis :name=
  [^Head this name]
  (reset! (.name this) name)
  this)

(def root (basis :clone))

(defmethod root :<<
  [^Head this name]
  (this [:set! name (head :name name :version (.version this))])
  this)

(comment
  (root [:get "welcome/index"])
  (root [:<< "welcome/index"])
  (send root (<< "welcome/index"))
  (name (root [:get "welcome/index"]))
  (name (send root (get "welcome/index")))
  (name (root [:name= "root"]))
  (name (root [:get "welcome/index"]))
  (basis :messages)
  (send basis messages)
  (root :messages)
  (basis :clone)
  (basis :id)
  (basis [:understands? :clone])
  (send basis (understands? :clone))
  (basis [:understands? :cloning])
  )
