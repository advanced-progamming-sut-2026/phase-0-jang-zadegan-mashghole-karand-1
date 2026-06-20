run:
	@find . -type f -name '*.class' -delete
	@javac Application.java
	@java Application.java
