(import '(java.net Socket ServerSocket))
(import '(java.io OutputStreamWriter InputStreamReader BufferedReader))

(defn on-thread
  "runs f on a new thread"
  [f]
  (doto (Thread. f) (.start)))

(defn create-server
  "creates a server on port, passing accepted sockets to accept-socket"
  [port accept-socket]
  (let [server-socket (ServerSocket. port)]
    (on-thread #(accept-socket (. server-socket accept)))
    server-socket))

(def prompt " > ")

(defn game-repl
  "runs a game loop on the given in/out streams"
  [in out]
  (binding [*ns* (create-ns 'mud)
	    *warn-on-reflection* false
	    *out* (OutputStreamWriter. out)]
    (let [eof (Object.)
	  r (BufferedReader. (InputStreamReader. in))]
	  (print prompt)
      (loop [line (. r readLine)]
	(when-not (= line "quit")
	  (println "Thanks for writing" line)
	  (print prompt)
	  (flush)
	  (recur (. r readLine)))))))

(defn handle-game-client
  "handles client socket"
  [client]
  (game-repl (. client getInputStream) (. client getOutputStream)))

(defn main-
  []
  (let [port (nth *command-line-args* 0)]
    (def server (create-server (Integer. port) handle-game-client))
    (println "Server started on port" port)
    (println "Thanks for playing")))

(main-)