sudo mongod -dbpath=/data/mongodb/db -logpath=/data/mongodb/log &
supervisor ../../e-homeland_server/bin/www
