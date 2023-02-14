# bus_got_lost

This is an API built from Spring Boot to make use of API endpoints provided by the Hong Kong Government to grab and format the Estimated Time of Arrivals (ETA) of buses for different bus routes at the nearest stop to the location provided.
I recently got involved in a Spring Boot training at the company I am working at so I thought why not build an API for Proof of Concept? I have always wanted to make use of the ETA API endpoints provided by the Government in my own app.
The concept is simple: the API receives a pair of geolocation (latitude, longitude) and find the nearest bus stop to that location. Then it provides the ETA of all the bus routes at that bus stop. The project is still on going as the return data of bus stops are more complicated than I thought. Despite its flaws in getting all the routes, it is still fully functional.
