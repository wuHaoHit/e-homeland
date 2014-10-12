var mongodb = require('./db');

function Admin(doc) {
    this.name = doc.id;
    this.password =doc.password;
    this.ip=doc.ip;
};
module.exports = Admin;

//存入Mongodb的文档
Admin.prototype.save = function save(callback) {
    var admin = {
        name: this.name,
        password: this.password,
        ip:this.ip,
    };

    mongodb.open(function(err, db) {
        if (err) {
            return callback(err);
        }
        //读取users集合
        db.collection('admin', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }
            // 为name属性添加索引
            // collection.ensureIndex('name', {unique: true});

            //写入user文档
            collection.insert(admin, {safe: true}, function(err, admin) {
                mongodb.close();
                callback(err, admin);
            });
        });
    });
};

Admin.update=function update(username,ip,callback){
    mongodb.open(function(err, db) {
    if (err) {
        return callback(err);
    }
    db.collection('admin', function(err, collection) {
           if (err) {
                mongodb.close();
                return callback(err);
            }
        collection.update({id:username},{$set:{ip:ip}},{safe: true}, function(err) {
            mongodb.close();
            callback(err);
        });
     });
    });
};

Admin.get = function get(username, callback) {
    mongodb.open(function(err, db) {
        if (err) {
            return callback(err);
        }
        //读取users集合
        db.collection('admin', function(err, collection) {
            if (err) {
                mongodb.close();
                return callback(err);
            }
         //查找id属性为username的文档
         collection.findOne({id : username}, function(err, doc) {
            mongodb.close();
            if (doc) {
                //封装文档为User对象
                var admin = new Admin(doc);
                callback(err, admin);
            } else {
                callback(err, null);
            }
        });
     });
    });
};