# uap-java
Is an implementation over https://github.com/ua-parser/uap-java
  - Can auto schedule updates every one our retrieving the yaml from a url.
  - It has a Guava-Cache implementation or can be extended to use others.
  - Parse agent strings and retriever, Device, OS and User Agent information.

Usage
----
To start the Parser with schedule and GuavaCache:

    Parser parser = new Parser()
    .cache(new GuavaCache())
    .startSchedule();
To start using it:

    parser.parse(....);

To stop the scheduling process:

    parser.stopSchedule();

License
----
Apache License http://www.apache.org/licenses/LICENSE-2.0
