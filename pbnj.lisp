(defmacro comment (&rest _))
(setf (fdefinition 'identical?) #'eq)
(setf (fdefinition 'nil?) #'null)
(setf (fdefinition 'string?) #'stringp)
(setf (fdefinition 'type?) #'typep)

(defstruct (action (:predicate action?))
  (namespace
   nil
   :type string
   :read-only t)
  (name
   nil
   :type string
   :read-only t)
  (meta
   nil
   :type list
   :read-only t))

(defun action (namespace name &optional meta)
  (if meta
    (make-action :namespace namespace :name name :meta meta
    (make-action :namespace namespace :name name)))

(defun action= (one another)
  (and
   (string= (action-namespace one) (action-namespace one))
   (string= (action-name one) (action-name one))))

(defun action->string (action)
  (concatenate 'string (action-namespace action) "#" (action-name action)))

(comment
 (setq root (action "welcome" "index"))
 (action? root)
 (type? root 'action)
 (action-name root)
 (action-namespace root)
 (action-meta root)
 (action->string root)

 (equal (action "welcome" "index") (action "welcome" "index"))
 (equalp (action "welcome" "index") (action "welcome" "index"))
 (identical? (action "welcome" "index") (action "welcome" "index"))
 (identical? root root)
 (eql (action "welcome" "index") (action "welcome" "index"))
 (action=
  (action "welcome" "index")
  (action "welcome" "index" '((hey "you"))))
 (string? "Hey")
 )
