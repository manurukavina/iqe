Internet Query Engine
=====================

IQE is a conceptual tool to allow users to retrieve specific data from websites or internet resources using a structured query language (SQL) interface.

The system is still under development, with a key component—query parsing—yet to be implemented. Query parsing would involve translating the SQL-like commands into actionable steps, such as sending HTTP requests, parsing HTML, or querying external APIs (e.g., DNS or SSL registries). Once complete, the engine could handle complex queries, including filtering, joining data from multiple sources, and aggregating results.

---

Potential features (once fully built):
- Fetch metadata (e.g., page titles, descriptions) from HTML headers.
- Extract content from specific webpage paths or elements (e.g., emails, text, links).
- Query server-related data (e.g., DNS records, SSL certificate details).
- Support for conditions (e.g., WHERE clauses) and multiple data sources.
- Output results in a tabular or structured format, similar to a database.

Challenges to address:
- Handling dynamic websites (e.g., JavaScript-rendered content).
- Rate limiting and ethical scraping considerations.
- Standardizing the schema for unstructured web data.

---

Command-Line Arguments:

| Option | Long Option | Description |
|--------|------------|-------------|
| `-p`   | `--port`   | Specifies the port number for the server (0-65535). Default: 8641. |
| `-b`   | `--backlog` | Sets the maximum number of pending connections. Default: 0. |
| `-t`   | `--threads` | Defines the number of threads in the thread pool. Must be non-negative. Default: 0. |
| `-d`   | `--debug`   | Enables debug mode, printing configuration details. |
| `-ng`  | `--noget`   | Disables the GET method in the application. |

Running the Application:
To start the application with default settings, use:
```sh
java -jar iqe-0.0.0.jar
```

To specify custom configurations, pass the desired options:
```sh
java -jar iqe-0.0.0.jar -p 8080 -b 50 -t 10 -d
```

This example:
- Runs the server on port `8080`
- Sets a backlog limit of `50`
- Allocates `10` threads for the thread pool
- Enables debug mode

---

Examples:
```
SELECT dns.ns, meta.title FROM example.com;
```
```
SELECT ssl.validuntil FROM example.com;
```
```
SELECT body.email FROM example.com WHERE path = 'about';
```
```
SELECT body.links, meta.keywords FROM example.com WHERE path = 'blog'
```
```
SELECT dns.mx, body.text FROM example.com WHERE body.text LIKE '%signup%';
```

