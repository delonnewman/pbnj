#lang racket
(require racket/generic)
(module+ test
  (require rackunit))

(provide
 (contract-out
  [action (-> string? string? list? action?)]
  [action-namespace (-> action? string?)]
  [action-name (-> action? string?)]
  [action-meta (-> action? list?)]
  [action->string (-> action? string?)]))

(define-generics self-describable
  (meta self-describable)
  (with-meta self-describable meta))

(struct action (namespace name meta)
  #:methods
  gen:equal+hash
  [(define (equal-proc this other equal?-recur)
     (and (equal?-recur (action-namespace this) (action-namespace other))
          (equal?-recur (action-name this) (action-name other))))
   (define (hash-proc this hash-recur)
     (+ (hash-recur (action-namespace this)) (* 3 (hash-recur (action-name this)))))
   (define (hash2-proc this hash2-recur)
     (+ (hash2-recur (action-namespace this) (action-name this))))]
  #:methods
  gen:self-describable
  [(define (meta this) (action-meta this))
   (define (with-meta this meta)
     (if (equal? meta (action-meta this))
         this
         (action (action-namespace this) (action-name this) meta)))])

(module+ test
  (define root (action "welcome" "index" '()))
  (let ((data '((hey "you"))))
    (check-equal? root (action "welcome" "index" data))
    (check-equal? data (meta (with-meta root data)))))

(define (action->string action)
  (string-append (action-namespace action) "#" (action-name action)))

(module+ test
  (check-equal? (action->string root) "welcome#index"))
