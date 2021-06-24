compile:
	gradle build

run:
	if [ '$(ARGS)' = '' ]; then gradle run; else gradle run --args='$(ARGS)'; fi
