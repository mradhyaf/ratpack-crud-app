# ratpack-crud-app

This is a backend application for file sharing built with Java, Ratpack, and SQLite.

## Table of Contents

1. [Quick Start](#1-quick-start)
2. [System Design](#2-system-design)
3. [API Reference](#3-api-reference)

## 1. Quick Start

Run with gradle wrapper:

```shell
$ ./gradlew run
```

## 2. System Design

This app consists of 3 components, shown by the image below.
![diagram](./docs/images/diagram.png)

### Summary of each component:

1. Server: a ratpack server that routes and handles the user requests.
2. Database: interfaces with sqlite through JDBC (Java Database Connectivity).
3. FileManager: manages file creation, deletion.

## 3. API Reference

The reference to the API is available
through [Postman](https://www.postman.com/docking-module-engineer-95800223/workspace/public/collection/41979206-dca4b294-4d0c-49de-8eed-a1bb58e94db0?action=share&creator=41979206).