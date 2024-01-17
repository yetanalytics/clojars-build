.PHONY: test-build

test-build:
	clojure -X:build :artifact-id '"clojars-build"' :version '"LATEST-SNAPSHOT"' :src-dirs '["src/main"]' :resource-dirs [] :github-repo '"yetanalytics/clojars-build"' :github-sha '"c49ec39b56e114aa63b4517df729846be0c75e0a"'

test-deploy:
	clojure -X:deploy :artifact-id '"clojars-build"' :version '"LATEST-SNAPSHOT"'
