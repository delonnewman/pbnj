(defstruct action :namespace :name :meta)

(defvar root (make-action :namespace "welcome" :name "index" :meta '()))
(action-name root)
(action-namespace root)
(action-meta root)

(defvar act (make-action))
(setf (action-name act) "hey")
act
