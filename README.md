# SnapMark

A developer's personal code snippet and bookmark manager REST API.

## Tech Stack

- Java 17
- Spring Boot 3.2
- SQLite (production) / H2 (test)
- Spring Data JPA

## Quick Start

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

## API Endpoints

### Snippets

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/snippets` | List all snippets |
| GET | `/api/snippets?tag=java` | Filter by tag |
| GET | `/api/snippets?language=python` | Filter by language |
| GET | `/api/snippets?keyword=hello` | Search by keyword |
| GET | `/api/snippets/{id}` | Get snippet by ID |
| POST | `/api/snippets` | Create a snippet |
| PUT | `/api/snippets/{id}` | Update a snippet |
| DELETE | `/api/snippets/{id}` | Delete a snippet |
| GET | `/api/snippets/tags` | List all tags |

### Bookmarks

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/bookmarks` | List all bookmarks |
| GET | `/api/bookmarks?tag=tech` | Filter by tag |
| GET | `/api/bookmarks?keyword=spring` | Search by keyword |
| GET | `/api/bookmarks/{id}` | Get bookmark by ID |
| POST | `/api/bookmarks` | Create a bookmark |
| PUT | `/api/bookmarks/{id}` | Update a bookmark |
| DELETE | `/api/bookmarks/{id}` | Delete a bookmark |

## Example

```bash
# Create a snippet
curl -X POST http://localhost:8080/api/snippets \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Quick Sort",
    "code": "public static void quickSort(int[] arr, int low, int high) { ... }",
    "language": "java",
    "tags": "java,algorithm,sorting",
    "description": "Quick sort implementation in Java"
  }'

# Filter by tag
curl http://localhost:8080/api/snippets?tag=java
```

## Contributing

See the [Issues](../../issues) page for open tasks. Contributions welcome!
