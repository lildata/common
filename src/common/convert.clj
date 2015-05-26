(ns common.convert
  "Type conversions.
  >> ctdean jimbru")

(defn to-keyword
  "Convert to a keyword arg."
  [x]
  (and x
       (if (keyword? x)
           x
           (keyword (str x)))))

(defn from-bytes
  "Convert from a byte array to a UTF-8 String. Returns nil is a
   array is given."
  [bytes]
  (when bytes
    (String. bytes)))

(defn to-bytes
  "Change a UTF-8 String to an array of bytes. Returns nil if a nil
   string is given."
  [s]
  (when s
    (.getBytes s)))

(defn str->int
  "Convert a string to an integral number."
  [s]
  (when s
    (if (number? s)
        s
        (Long/parseLong s))))

(defn str->uuid
  "Converts a string to a UUID object."
  [s]
  (java.util.UUID/fromString s))

(defn uuid->str
  "Converts a UUID object to a string."
  [uuid]
  (.toString uuid))
