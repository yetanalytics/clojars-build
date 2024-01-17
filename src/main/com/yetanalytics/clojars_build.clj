(ns com.yetanalytics.clojars-build
  (:require [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as dd]))

(def default-group-id "com.yetanalytics")

(def default-src-dirs ["src/main"])

(def default-resource-dirs ["resources"])

(def default-license-name "Apache-2.0")

(def default-license-url "https://www.apache.org/licenses/LICENSE-2.0.txt")

(defn- basis []
  (b/create-basis {}))

(def class-dir "target/classes")

(defn- library-name [group-id artifact-id]
  (format "%s/%s" group-id artifact-id))

(defn- jar-file-name [artifact-id version]
  (format "target/%s-%s.jar" artifact-id version))

(defn- github-url [github-repo]
  (format "https://github.com/%s" github-repo))

(defn- github-conn [github-repo]
  (format "scm:git:git://github.com/%s.git" github-repo))

(defn- github-dev-conn [github-repo]
  (format "scm:git:ssh://git@github.com/%s.git" github-repo))

(defn jar
  "Create the JAR file that will be deployed onto Clojars.
   
   | Parameters      | Description 
   | ---             | ---
   | `group-id`      | Clojars group ID. Defaults to `com.yetanalytics`.
   | `artifact-id`   | Clojars artifact ID, i.e. the library name. No default.
   | `version`       | Version string. No default.
   | `src-dirs`      | Source directory vector. Defaults to `[\"src/main\"]`.
   | `resource-dirs` | Resource directory vector. Defaults to `[\"resources\"]`.
   | `license-name`  | License name. Defaults to `Apache-2.0`.
   | `license-url`   | License URL. Defaults to `https://www.apache.org/licenses/LICENSE-2.0.txt`.
   | `github-repo`   | Github repository name. No default.
   | `github-sha`    | Github SHA string. No default."
  [{:keys [group-id
           artifact-id
           version
           src-dirs
           resource-dirs
           license-name
           license-url
           github-repo
           github-sha]
    :or {group-id      default-group-id
         src-dirs      default-src-dirs
         resource-dirs default-resource-dirs
         license-name  default-license-name
         license-url   default-license-url}}]
  (let [all-dirs (vec (concat src-dirs resource-dirs))
        lib-name (library-name group-id artifact-id)
        lib-sym  (symbol lib-name)
        scm-map  {:url                 (github-url github-repo)
                  :tag                 github-sha
                  :connection          (github-conn github-repo)
                  :developerConnection (github-dev-conn github-repo)}
        pom-data [[:licenses
                   [:license
                    [:name license-name]
                    [:url license-url]]]]
        jar-name (jar-file-name artifact-id version)]
    (println "Writing pom.xml")
    (b/write-pom
     {:basis         (basis)
      :class-dir     class-dir
      :lib           lib-sym
      :version       version
      :scm           scm-map
      :src-dirs      src-dirs
      :resource-dirs resource-dirs
      :pom-data      pom-data})
    (println "Copying directories:" all-dirs)
    (b/copy-dir
     {:src-dirs   all-dirs
      :target-dir class-dir})
    (println "Creating JAR file:" jar-name)
    (b/jar
     {:class-dir class-dir
      :jar-file  jar-name})
    nil))

(defn deploy
  "Deploy the JAR file (which should have been created using `jar` and located
   at `target/[lib-name]-[version].jar`) to clojars.
   
   | Parameters      | Description 
   | ---             | ---
   | `group-id`      | Clojars group ID. Defaults to `com.yetanalytics`.
   | `artifact-id`   | Clojars artifact ID, i.e. the library name. No default.
   | `version`       | Version string. No default."
  [{:keys [group-id
           artifact-id
           version]
    :or {group-id default-group-id}}]
  (let [lib-name (library-name group-id artifact-id)
        lib-sym  (symbol lib-name)
        jar-name (jar-file-name artifact-id version)]
    (dd/deploy {:installer :remote
                :artifact  (b/resolve-path jar-name)
                :pom-file  (b/pom-path {:lib       lib-sym
                                        :class-dir class-dir})})))

(comment
  ;; These comments both demonstrate the usage of `jar` and `deploy`,
  ;; and can be used to build and deploy this very library to Clojars.

  (jar
   {:artifact-id   "action-deploy-clojars"
    :version       "2.0.0-SNAPSHOT"
    :src-dirs      ["src/main"]
    :resource-dirs []
    :github-repo   "yetanalytics/action-deploy-clojars"
    :github-sha    "6997fb7a489a938b1cb743830e5db6cb2cbd7931"})
  
  (deploy
   {:artifact-id "action-deploy-clojars"
    :version     "2.0.0-SNAPSHOT"}))
