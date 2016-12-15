# The FROM instruction sets the Base Image for subsequent instructions.
# Using Nginx as Base Image
FROM flurdy/activator:1.3.7
MAINTAINER Xiren <xiren_88@126.com>

# The RUN instruction will execute any commands
# Adding HelloWorld page into Nginx server
COPY . /usr/local/Apollo
WORKDIR /usr/local/Apollo
RUN activator dist
WORKDIR /usr/local/Apollo/target/universal
RUN unzip ./apollo-1.0-SNAPSHOT.zip
WORKDIR /usr/local/Apollo/target/universal/apollo-1.0-SNAPSHOT/bin

# The EXPOSE instruction informs Docker that the container listens on the specified network ports at runtime
EXPOSE 9000

# The CMD instruction provides default execution command for an container
# Start Nginx and keep it from running background
CMD sh apollo