## Audition

Audition is a tool for developers to inspect and modify components at runtime with some easy tooling directly in the browser.

It shows a list of available components, lets you inspect one in particular and change it's args in real-time.

Example:

```clojurescript
(defn $error
  {:audition {:args [:string]}}
  [& error-msgs]
  [:div.error-message
   error-msgs])
```

The metadata part lets Audition know what arguments are modifiable, and what values they carry.

If you want to customize the default values, you can extend the `audition.params/default-args` multimethod, like this:

```clojurescript
(defmethod audition.params/default-args :git-item []
  {:commitHash "ca34d74f8729c42a6aa3f89eefd578420c696471",
   :subject "init"})
```

With this placed after your component, you can now use the `:git-item` keyword to use whatever it returns as the default value. Don't forget to include `(:require [audition.params])` in your ns declaration at the top of the file.