#Configuring Nginx

###Install Nginx with SPDY 3 support

- Following instructions [found here](https://bjornjohansen.no/install-latest-version-of-nginx-on-ubuntu), download and install Nginx with SPDY3 module enabled:

        curl http://nginx.org/keys/nginx_signing.key | apt-key add -
        echo -e "deb http://nginx.org/packages/mainline/ubuntu/ `lsb_release -cs` nginx\ndeb-src http://nginx.org/packages/mainline/ubuntu/ `lsb_release -cs` nginx" > /etc/apt/sources.list.d/nginx.list
        sudo apt-get install spdy
        
###Create and install SSL certificates
- [copy/create your SSL certificates](https://www.startssl.com/?app=42), and put them in `/etc/nginx/ssl/{site url}/.` 

###Install all the site configurations
- We have the following apps:

  - **Browser** (Deprecated: Tomcat, MySQL, Java 7, Solr), 
  - **Search** (Deprecated: Tomcat, MySQL, Java 7, Solr), 
  - **Jenkins Build Server** (Java), 
  - **Refset Client** (NodeJS, Solr), 
  - **Solr** (Tomcat, MySQL, Java), 
  - **Refset API** (Tomcat, MySQL, Java 7), 
  - **Sparql API** (Fuseki, Java)

- You might want to deploy these on to different servers, as they are standalone apps

- Download the site configs from our [Github repository](https://github.com/IHTSDO/snomed-publish/tree/master/config/nginx)

- You will need to change the filenames / server names / SSL certificate location and name to match the URL the app(s) will be deployed to
