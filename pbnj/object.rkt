#lang racket

(struct object ())

(struct message ())
(struct single-message message ())
(struct keyword-message message ())
(struct argument-list-message message ())
