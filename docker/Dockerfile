FROM flurdy/activator:1.3.7
ADD ../Apollo /usr/local/Apollo
RUN cd /usr/local/Apollo
RUN activator
RUN dist
RUN cd /usr/local/Apollo/target/universal
RUN unzip ./apollo-1.0-SNAPSHOT.zip
RUN cd ./apollo-1.0-SNAPSHOT/bin
CMD sh apollo