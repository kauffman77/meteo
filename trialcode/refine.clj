(ns notes
  (:require (clojure [zip :as zip]
		     [xml :as xml])))

(defn refine [meas ctx time]
  (let [nmeas (atom meas)
	comp (future (iterative-refine nmeas ctx))]
    (Thread/sleep time)			;Sleep until timeout
    (when (not (future-done? comp))	;Shut thread down if still running
      (future-cancel comp))
    @nmeas))

(defn refine [time]
  (let [a (atom 0)			;Data structure
	f (future
	   (while true			;The work to be done
	     (dosync (swap! a inc))
	     (Thread/sleep 0)))]	;Check for continuation
    (Thread/sleep time)			;Sleep until timeout
    (when (not (future-done? f))	;Shut thread down if still running
      (future-cancel f))
    @a))

(defn refine [init work timeout]
  (let [cur (ref init)			;Data structure
	cont (ref true)
	fut (future
	     (while @cont
	       (let [nxt (work @cur)]
		 (dosync (ref-set cur nxt)))))]
    (Thread/sleep timeout)		;Sleep until timeout
    (dosync (ref-set cont false))
    @cur))


;; Working version
(defn refine [init work timeout]
  (let [cur (atom init)			;Set initial state
	fut (future			;Spawn worker thread
	     (while true
	       (dosync (swap! cur work))
	       (Thread/sleep 0)))]	;Worker: pause to see if cancelled
    (Thread/sleep timeout)		;Main: Sleep until timeout
    (when (not (future-done? fut))	;Main: stop worker if still running
      (future-cancel fut))
    @cur))

