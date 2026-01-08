(ns pbnj.data
  (:refer-clojure :exclude [send]))

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


(def basic-methods (atom {} :validator map?))
(swap! basic-methods assoc :version (fn [^Head obj] (.version obj)))
(swap! basic-methods assoc :name=
  (fn [^Head obj name]
    (reset! (.name obj) name)
    obj))

(defn head
  [& {:keys [id name version methods attributes]
      :or {id (random-uuid)
           name nil
           version 0
           methods (atom @basic-methods :validator map?)
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

(def root (head :name "root"))

(defn add-method! [^Head node msg f]
  (swap! (.methods node) assoc msg f)
  node)

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

(add-method! root :add-ref
  (fn add-ref [^Head obj name]
    (swap! (.attributes obj) assoc name (head :name name :version (:version root)))
    obj))

(add-method! root :get-ref
  (fn get-ref [^Head obj name]
    (get @(.attributes obj) name)))

(comment
  (root :get-ref "welcome/index")
  (root :add-ref "welcome/index")
  (name (root :get-ref "welcome/index"))
  (name (root :name= "root"))
  (name (root :get-ref "welcome/index"))
  )
