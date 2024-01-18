.PHONY: clean test-build test-deploy build deploy

clean:
	rm -rf target

test-build:
	clojure -X:build :artifact-id '"clojars-build"' :version '"LATEST-SNAPSHOT"' :src-dirs '["src/main"]' :resource-dirs [] :github-repo '"yetanalytics/clojars-build"' :github-sha '"c49ec39b56e114aa63b4517df729846be0c75e0a"'

build:
	clojure -X:build :artifact-id '"clojars-build"' :version '"$(VERSION)"' :src-dirs '["src/main"]' :resource-dirs [] :github-repo '"yetanalytics/clojars-build"' :github-sha '"$(GITHUB_SHA)"'

test-deploy:
	clojure -X:deploy :artifact-id '"clojars-build"' :version '"LATEST-SNAPSHOT"'

deploy:
	clojure -X:deploy :artifact-id '"clojars-build"' :version '"$(VERSION)"'
