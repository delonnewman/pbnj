(ns pbnj.data
  (:refer-clojure :exclude [send defmethod]))

(set! *warn-on-reflection* true)

(defprotocol Obj
  (find-method [this msg]
    "Return the method that corresponds to the message or nil"))

(def send nil)

(deftype Head
  [id name version methods attributes]
  Obj
  (find-method [this msg]
    (get @(.methods this) msg))

  clojure.lang.Named
  (getNamespace [this] nil)
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
  (applyTo [this args] (apply send this args))
  (invoke [this msg] (send this msg))
  (invoke [this msg a] (send this msg a))
  (invoke [this msg a b] (send this msg a b))
  (invoke [this msg a b c] (send this msg a b c))
  (invoke [this msg a b c d] (send this msg a b c d))
  (invoke [this msg a b c d e] (send this msg a b c d e))
  (invoke [this msg a b c d e f] (send this msg a b c d e f))
  (invoke [this msg a b c d e f g] (send this msg a b c d e f g))
  (invoke [this msg a b c d e f g h] (send this msg a b c d e f g h))
  (invoke [this msg a b c d e f g h i] (send this msg a b c d e f g h i))
  (invoke [this msg a b c d e f g h i j] (send this msg a b c d e f g h i j))
  (invoke [this msg a b c d e f g h i j k] (send this msg a b c d e f g h i j k))
  (invoke [this msg a b c d e f g h i j k l] (send this msg a b c d e f g h i j k l))
  (invoke [this msg a b c d e f g h i j k l m] (send this msg a b c d e f g h i j k l m))
  (invoke [this msg a b c d e f g h i j k l m n] (send this msg a b c d e f g h i j k l m n))
  (invoke [this msg a b c d e f g h i j k l m n o] (send this msg a b c d e f g h i j k l m n o))
  (invoke [this msg a b c d e f g h i j k l m n o p] (send this msg a b c d e f g h i j k l m n o p))
  (invoke [this msg a b c d e f g h i j k l m n o p q] (send this msg a b c d e f g h i j k l m n o p q))
  (invoke [this msg a b c d e f g h i j k l m n o p q r] (send this msg a b c d e f g h i j k l m n o p q r))
  (invoke [this msg a b c d e f g h i j k l m n o p q r s] (send this msg a b c d e f g h i j k l m n o p q r s))
  (invoke [this msg a b c d e f g h i j k l m n o p q r s rest] (send this msg a b c d e f g h i j k l m n o p q r s rest)))


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

(defn send [obj msg & args]
  (let [method (find-method obj msg)]
    (if (and method (fn? method))
      (apply method obj args)
      (if (method? obj :method-not-found)
        (send obj :method-not-found msg args)
        (throw (ex-info (str "Method " msg " not found") {:obj obj :msg msg}))))))

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

(defmethod basis :set!
  [^Head this key value]
  (swap! (.attributes this) assoc key value)
  this)

(add-method! basis :clone clone)

(add-method! basis :messages
  (fn messages [^Head this] (keys @(.methods this))))

(add-method! basis :id
  (fn name [^Head this] @(.id this)))

(add-method! basis :name
  (fn name [^Head this] @(.name this)))

(defmethod basis :name=
  [^Head this name]
  (reset! (.name this) name)
  this)

(def root (basis :clone))

(defmethod root :add-ref
  [^Head this name]
  (swap! (.attributes this)
         assoc name (head :name name :version (.version this)))
  this)

(defmethod root :get-ref
  [^Head this name]
  (get @(.attributes this) name))

(comment
  (root :get-ref "welcome/index")
  (root :add-ref "welcome/index")
  (name (root :get-ref "welcome/index"))
  (name (root :name= "root"))
  (name (root :get-ref "welcome/index"))
  (basis :messages)
  (root :messages)
  (basis :clone)
  )
