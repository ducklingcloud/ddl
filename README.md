Duckling Document Library (DDL)
===============================

To build, 'localdep' and then 'mvn package'.

Usually, building umt, vmt, clb, and ddl in order is a good idea.

Setup
-----

1) Have vwbconfig.properties from its TEMPLATE and edit it.

2) In mysql, create databases and tables, using both
WEB-INF/sql/ddl_schema-xxx.sql and ddlsub_schema-xxx.sql.

3) Adjust configs related to umt/vmt/clb...

4) sphinx, dlog, etc. are optional.

5) Install nginx-ddl refering to nginx-ddl/nginx.conf. Cooperation
between nginx-ddl and the nginx-clb (clbs.escience.cn) is
required. There are redirections defined in the nginx on DDL's side.

For example, umt/vmt/clb/ddl's tomcat listens on 8080, nginx-clb(clbs)
on 8089, and nginx-ddl on 8090. The nginx-ddl is a reverse-proxy for
ddl, while it could also be proxy for umt/vmt/clb as well.

6) Finally, DDL works! It is the most important application service of
Duckling, IMHO.

-appleii@20160626


Fork.  -appleii@20170317


