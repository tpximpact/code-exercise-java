# URL Shortener Coding Exercise

## Initial Thoughts
Before starting the actual implementation, here are some initial thoughts and design decisions that will shape the final solution:

* The tech-stack:
  * React will be used for the Front-End
  * Java Spring Boot will be used for the Back-End
  * H2 will be used as an in memory database for simplicity sake. This can be configured through docker-compose env variables and could theoretically be swapped to a different database (Aurora/RDS) in a production environment.
  * With the simplicity of the application (Create, Read, Delete operatations), there isn't any real need to break the backend up into microservices. 
* Test Driven Development will be closely followed as to ensure high amounts of meaningful code coverage on implementation. 
* Certain performance decisions can be taken which are easy to implement in the Backend:
  * Both the requested URL and the shortened url should be stored in the Database as HASH indexes, this will allow for O(1) lookups
  * Use caching strategies to alleviate database congestion and improve through-put (in the event that this was to become popular and used frequently). 
