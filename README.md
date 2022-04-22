#Spring Boot Starter Project
####*This RESTful API of a budget tracker is a starter project that incorporates and covers the following technologies and concepts...*
1. **Connection and linking to a database hosted locally on a MySQL Server**
2. **JSON Web Token authentication, authorization, and flow**
3. **Models of entities with relationships**
4. **Singleton Design Pattern**
5. **Unit Testing with JUnit and Mockito**
6. **Request Mappings with proper error handling**

##Steps Taken to Initialize the Spring Project
In order to initialize a Spring Project like this one, visit [start.spring.io](start.spring.io). 
The plugin that this project exemplifies is Maven and is coded in Java.

Choosing the default/most complete version of Spring Boot is optimal rather than the various
*SNAPSHOT* versions. ***Group*** name can be a username of sorts, ***Artifact***
would be the name of the project which in this case is titled *budget-tracker*.

The ***Packaging*** option that is most often chosen is *Jar*.
Picking the *Jar* option helps make the packaging and 
deployment of the application much easier.

Lastly the ***Java*** version should be the most up to date one.
Make sure that your JDK is up to date but if it is not or if the newest build
cannot be installed then there are steps that can be taken to modify the version of Java
within the IDE to match your currently downloaded JDK in order to work around that.

The ***Dependencies*** added for the initialization of this project
were *Spring Data JPA*, *Spring Security*, *Spring Web*, *MySQL Driver*, and *Lombok*.

The final step is to click ***Generate***.

###Fixing the Version of Java (if necessary)
Once the project is opened in IntelliJ, open up the pom.xml and find
where it says `<java.version>'version'</java.version>`. Change the
number to whatever version of Java you are currently using and *load
the maven changes*.

Use the shortcut `ctrl`+`alt`+`s`, click on **Build, Execution, Deployment** ⇒
**Compiler** ⇒ **Java Compiler**. 

In the **Module** section, make sure that the ***Target Bytecode Version***
for your project module is matching the same JDK that you are currently using
and do the same for the section **Project Bytecode Version** above the module section.

Apply the changes and then you'll be set.

##Installing MySQL and Linking to Spring Project
###MySQL Installation
####*If not installed already, download the [MySQL Installer (Web Community)](https://dev.mysql.com/downloads/installer/)...*
1) In the installer, choose the **Custom** option and hit *Next*.
2) Add the correct server, application, and connector features to be installed...
    1) Expand **MySQL Servers** ⇒ **MySQL Server** ⇒ **MySQL Server** *latest server version* ⇒ Click on
       **MySQL Server** *latest server version* **-X64** and click green arrow to drag it over to be installed
    2) Expand **Applications** ⇒ **MySQL Workbench** ⇒ **MySQL Workbench** *latest server version* ⇒ Click on
       **MySQL Workbench** *latest server version* **-X64** and click green arrow to drag it over to be installed
    3) Expand **Applications** ⇒ **MySQL Shell** ⇒ **MySQL Shell** *latest server version* ⇒ Click on
       **MySQL Shell** *latest server version* **-X64** and click green arrow to drag it over to be installed
    4) Expand **MySQL Connectors** ⇒ Click on **JDBC Driver for MySQL (Connector/J)** or
       whatever option fits closest and click green arrow to drag it over to be installed
    5) Click on *Next*
3) Click *Execute* to check the requirements for the features and if it asks to install ***Microsoft
   Visual C++ 2015-2019***, click *agree* and then *Install*. After it is done, click *Next*
4) They are ready to download so click *Execute* to download the features. After all downloads
   are complete, click *Next*
5) If the installation concludes and you are forced to click *Finish*, open the ***MySQL Community Installer***
   application in order to configure the MySQL Server
   1) On the **Types and Networking** page, verify the *Port* is set to 3306 keep everything the same and click *Next*
   2) On the **Authentication Method** page, select *Use Strong Password Encryption* and click *Next*
   3) On the **Accounts and Roles** page, enter in and confirm your password for the Root Account (you don't have to *Add User*)
   4) Click on *Next*
   5) Click *Execute* and finish up the rest of the configuration
6) You should now be able to open up the ***MySQL Workbench*** application
7) You can rename your MySQL Connection which should be linked to the *root* user and *localhost:3306*
8) Open up this connection by entering the password you made during the configuration
9) Click on the *Schemas* section in the bottom of the left side, right click the section, and create a new schema
   with the name of your choosing. For **Charset/Collation** select *utf8mb4* for the first dropdown and the first option under the Default
   *(utf8mb4_0900_...)* for the second dropdown

You should now have MySQL set up with your schema you will be using 
and it should be completely ready to link to the Spring Project.

###Connecting MySQL Schema to Spring Boot Project
Back in the Spring Project, use the file explorer on the left 
to locate ***application.properties*** ( **src** ⇒ **main** ⇒ **resources** ⇒ ***application.properties*** ).

Add the following code to the file...
```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url = jdbc:mysql://localhost:3306/name_of_schema
spring.datasource.username = root
spring.datasource.password = Password
spring.jpa.show-sql = true
spring.jpa.hibernate.ddl-auto = update
spring.jpa.database-platform = org.hibernate.dialect.MySQL5Dialect
server.port = 5000
spring.jpa.properties.max_allowed_packet = 2000
```
Obviously 'name_of_schema' will be the actual name you gave to your schema you created and that will hold the relational databases.
Password will also be the password you made for the root user during configuration.

`spring.jpa.hibernate.ddl-auto` is extremely important for the creation, deletion, and updating of your tables everytime
the main application runs...
* Setting this to `update` is safe and will update the schema with new tables and can
update tables with new fields/attributes as well BUT it will not delete fields if the user
removes fields/attributes from the Model.java class in the Spring Project
* `create-drop` will create or initialize the tables to brand new when the main application runs and will drop *(delete)* tables and all existing data every time the main application stops running
*(typically used in test scenarios when mock data is being used)*
* `none` will not do anything to the tables other than insert new data entries for any existing table *(particularly used during production
when the application has been deployed)*

`server.port = 5000` specifies and establishes a new connection on the specified port when the main application runs.
Requests can then be made to *localhost:5000/...*

##JSON Web Token Access for Users
This section covers the JSON Web Token authentication and authorization flow.
This will not only allow each user to access the API but will only allow them
to retrieve data pertaining to them.

The java classes that will be explained are all in the ***security*** package in the main java source directory.

*It should be noted that the dependency ***com.auth0.jwt*** must be added to the maven project before implementing the JWT flow*

###Defining The BCrypt Bean
```java
@SpringBootApplication
public class BudgetTrackerApplication {

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(BudgetTrackerApplication.class, args);
	}

}
```
In `BudgetTrackerApplication.java`, we define a new *Bean* in order to initialize our BCryptPasswordEncoder object 
that is responsible for configuring and hashing the User's password. Defining this *Bean* in the main application 
program file allows an instance of that object to be created first thing as soon as the API starts running.

###The Authentication Filter
Authenticating the user attempting to make requests to the API using correct valid credentials is an integral 
part of security and allowing permission to access the API and its routes.

####*The steps to implementing proper authentication are as follows...*
Having a JWT Authorization Filter that is a subclass *(extends)* the ***UsernamePasswordAuthenticationFilter*** *(default class for password authentication in Spring Security)*.
```java
private AuthenticationManager authenticationManager;

public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
}
```
* The constructor initializes the only variable *authenticationManager* which is responsible for managing and validating
  credentials of a user as well as encoding the password to verify the password

```java
@Override
public Authentication attemptAuthentication(HttpServletRequest req,
    HttpServletResponse res) throws AuthenticationException {
    try {
        ApplicationUser creds = new ObjectMapper()
        .readValue(req.getInputStream(), ApplicationUser.class);
    
        return authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
        creds.getUsername(),
        creds.getPassword(),
        new ArrayList<>())
    );
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
```
* Overriding the *attemptAuthentication* function from the ***UsernameAuthenticationFilter*** class allows us
  to redefine what this function does. This function will execute when the user attempts to login to the application and
  will read the credentials *(username and password)*, will create a user from them, and then will check if the credentials
  can be authenticated.
* The username and password are passed to a new list that (if defined later) can represent a user role.

```java
@Override
protected void successfulAuthentication(HttpServletRequest req,
                                        HttpServletResponse res,
                                        FilterChain chain,
                                        Authentication auth) throws IOException, ServletException {

    String token = JWT.create()
            .withSubject(((User) auth.getPrincipal()).getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    String body = /* ((User) auth.getPrincipal()).getUsername()+": " + */ token;
    res.getWriter().write(body);
    res.getWriter().flush();
}
```
* Overriding the *successfulAuthentication* function from the ***UsernameAuthenticationFilter*** class allows the filter
  to validate the login attempt and pass parameters *(the String of the token to continue to access the app and the body which contains the token)*
  by Spring Security in order to permit access to the application and its routes.
* The *EXPIRATION_TIME* constant that is imported from the `SecurityConstants.java` class represents how many
  milliseconds the token is active for after generation. This can be changed in the `SecurityConstants.java` class
* This token will get written to a body that can also be modified to any liking. The body will then get written to the
  response and flushed out for the user to see after the successful login
  
###The Authorization Filter
After preparing the `JWTAuthenticationFilter.java`, an Authorization filter is needed in order to actually permit
the user with a valid token to use the application. 
* The constructor again is used to initialize the `AuthenticationManager authManager` variable that will handle
the credentials.

```java
@Override
protected void doFilterInternal(HttpServletRequest req,
                                HttpServletResponse res,
                                FilterChain chain) throws IOException, ServletException {
    String header = req.getHeader(HEADER_STRING);

    if (header == null || !header.startsWith(TOKEN_PREFIX)) {
        chain.doFilter(req, res);
        return;
    }

    UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

    SecurityContextHolder.getContext().setAuthentication(authentication);
    chain.doFilter(req, res);
}
```
* The *doFilterInternal* method overrides the method originally from the ***BasicAuthenticationFilter*** class
  in order to intercept the request that was made and check to see if the request provided specified *HEADER_STRING*
  *(as predefined in the `SecurityConstants.java` class)* which in this case is the *BEARER* header
* If the token is present, then the method will continue and will call the *getAuthentication* method

```java
private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    String token = request.getHeader(HEADER_STRING);
    if (token != null) {
        // parse the token.
        String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .getSubject();

        if (user != null) {
            return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        }
        return null;
    }
    return null;
}
```
* This method is responsible for verifying the JSON Web Token and, if the token is valid, will reverse engineer 
  the token to attempt to get the user that the token was specially made for
* The authentication will get passed back to the *doFilterInternal* method and will be saved to the SecurityContext

###The Web Security Configuration
This class is the heart of the JWT feature that will implement both the Authorization and Authentication.

```java
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
```
* Annotating with `@EnableWebSecurity` and adding `extends WebSecurityConfigurerAdapter` enables custom security
  logic to be implemented
* Autowiring the objects of classes *UserDetailsServiceImpl* and *BCryptPasswordEncoder* will automatically construct
  and initialize instances of these objects so that we can use their methods and apply their logic for getting the User details
  as well as using the specific password encoder to hash and/or match the password
* The method *configure* that takes in an *HttpSecurity* object as a parameter is the most important in order to 
  clarify the specific secure endpoints and filters we want to apply *(while configuring CORS)*. This is also extremely
  important for allowing anyone, regardless of authentication/authorization, to be able to access our *SIGN_UP_URL* as defined
  in the `SecurityConstants.java` file. Both filters are applied as well to complete the configuration
* The other *configure* method is used to configure the AuthenticationManager in order to use the **bCryptPasswordEncoder**
  object as the password encoder while also checking and verifying credentials with the help of **userDetailsService**
  * The `UserDetailsService.java` class *(located in the 'user' package)* allows access for checking the repository
    of all users in order to help find and validate the user
    
With all the filters defined and implemented properly through `WebSecurity.java` the application is completely
set up with proper JWT Authentication and Authorization.

Any function and any function implementing a request mapping can now take `Authentication auth` as a parameter.
You can then use `auth.getName()` to get the username of the logged in or Authorized User and can then use a method within
the ***ApplicationUserRepository*** to take in the username as a parameter *(for making a query)* and return the ApplicationUser object
pertaining to the user logged in.

