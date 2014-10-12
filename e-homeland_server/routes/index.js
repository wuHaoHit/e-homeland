var express = require('express');
var User = require('../models/user.js');
var Post = require("../models/post.js");
var Admin=require('../models/admin.js');
var crypto = require('crypto');
var fs = require('fs');
var mongodb = require('../models/db');

var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {
	if (req.session.admin) {
		var username=req.session.admin.name;
		var password=req.session.admin.password;
		var ip=req.connection.remoteAddress;
		var sessionValid=true;

		Admin.get(username, function(err, admin) {
			if (!admin) {
				req.flash('error', '用户不存在');
				console.log("[ERROR] ip "+ip+" is trying to login with invalid id : "+ username+" . "+new Date());
				sessionValid=false;
			}
			if (admin.password != password) {
				req.flash('error', '用户名或密码错误');
				console.log("[ERROR] ip "+ip+" is trying to login the account : "+ username+"  with the wrong password. "+new Date());
				console.log("name/password error!");
				sessionValid=false;
			}

			if( !admin.ip || admin.ip!=ip){
				req.flash('error','你的帐号已在别处登录');
				console.log("[ERROR] user "+username+" is kicked out by "+admin.ip+" . "+new Date());
				sessionValid=false;
			}

			if(sessionValid){
				res.render('index', { title: 'E-homeland',backstage :'后台',login:'/backstage' });
			}else{
				res.render('index', { title: 'E-homeland',backstage :'员工登录',login:'#Staff-Modal' });
			}
		});
	}else{
		res.render('index', { title: 'E-homeland',backstage :'员工登录',login:'#Staff-Modal' });
	}
});

/*                  INDEX                      */
router.get('/indexControlCenter',function(req,res){
	var param=req.query.test;
	var date = new Date();
	//console.log(req.query.test);
	//获取参数并运算求取结果
	//var json=
	//{"index" :[
	//	{img : "/images/0.jpg",content : "test",news : "agxcul",count : 1},
	//	{img : "/images/0.jpg",content : "test",news : "agxcul",count : 2},
	//	{img : "/images/0.jpg",content : "test",news : "agxcul",count : 3},
	//]};

	res.render('xml', { 
		url_one: 'http://192.168.1.125:3000/images/me.gif',
		url_two:'http://192.168.1.125:3000/images/lika.jpg',
		url_three:'http://192.168.1.125:3000/images/s3.jpg',
		content: 'test # This example is a quick exercise to illustrate how the default, static and fixed to top navbar work. It includes the responsive CSS and HTML, so it also adapts to your viewport and device.' ,
		news:'agxcul',
		count:2,
		date: date
	});
});

router.post('/image-upload', function(req,res){
	var fs = require('fs');
	var name; 
	var user=req.session.admin.name;
	var album=req.body.album;

	if(req.body.imgname){
		name=req.body.imgname+"."+req.files.image.extension;
	}else{
		name=req.files.image.originalname;
	}

	var path='public/uploads/' +user;//user图片池
	fs.mkdir(path,function(callback){
		console.log("[TASK] user "+user +" is creating his own image pond. "+new Date());
	});
	var subpath=path+"/"+album;//子相册
	var tmpPath =  req.files.image.path;
	var targetPath = subpath+"/"+ name;
	fs.mkdir(subpath,function(callback){
		console.log("[TASK] user "+user +" is creating album : ["+album+"] . "+new Date());
	});
	fs.rename(tmpPath, targetPath , function(err) {
		if(err){
			throw err;
		}
		console.log("[TASK] user "+user +" upload image ["+name+"]  to album : ["+album+" ] finished . "+new Date());
            	//将当前的用户写到会话中
            	res.send("upload success! <a href='../''>home</a>");
            })
});

router.post('/helpInformation',function(req,res){
	console.log(req.body);
	console.log(req.files);
	res.send("200");

	var fs = require('fs');
	var name=req.files.tcy.originalname;
	var user="test";
	console.log("[TASK] user "+user +" is publishing his help . "+new Date());
	var path='public/help/';
	var tmpPath =  req.files.tcy.path;
	var targetPath = path+"/"+ name;

	fs.rename(tmpPath, targetPath , function(err) {
		if(err){
			throw err;
		}
		console.log("[TASK] user "+user +" publish his help finished . "+new Date());
		res.send("upload success! <a href='../''>home</a>");
	});
});

router.get('/getHelpList',function(req,res){
	fs.readdir("./public/help/", function (err,files){
		if(err){
			throw err;
		}  
		var cnt = files.length;
		var result={"files":[]};
		result.files+='[';
		files.forEach(function (filename){
			result.files+="{ name : ' "+filename+" '},";     		
		});
		result.files+=']';
		res.send(result);
		console.log(result);
	});
});

router.post('/publishhelp',function(req,res){
	console.log(req.files);
	console.log(req.body);
	var name=req.body.name;
	console.log(req.body.);

	mongodb.open(function(err, db) {
		if (err) {
			return callback(err);
		}

		db.collection('help', function(err, collection) {
			if (err) {
				mongodb.close();
				return callback(err);
			}
			var help={
				name:name,

			}

			collection.findOne({"name" : name}, function(err, doc) {
				if (doc) {
					res.writeHead(200, {
						'Content-Length': doc.file.length,
						'Content-Type': 'multipart' });
					res.write(doc.file,'binary');
					res.end();
					fs.writeFile(filePath, doc.file, 'binary', function(err){
						if(err){
							throw err;
						}
					});
				}
				mongodb.close();
			});
		});
	});
});


router.get('/gethelp',function(req,res){
	var random=parseInt(Math.random()*10);
	var Path="/home/gxcissad/nodejs/e-homeland/public/images/1.png";
	var path = require("path");

	var filePath='/home/gxcissad/nodejs/e-homeland/public/cache/2.tcy';
	var name;

	fs.readdir("./public/images/", function (err,files){
		if(err){
			throw err;
		}else{
			Path="/home/gxcissad/nodejs/e-homeland/public/images/"+files[random];
			name=files[random];
		}
	});

	fs.exists(Path, function(exists){
		if (exists) {
			//res.sendfile(Path);

			fs.readFile(Path, "binary", function(err, file) {
				var img={
					"name":name,
					"file":file,
				};

				mongodb.open(function(err, db) {
					if (err) {
						return callback(err);
					}

					db.collection('images', function(err, collection) {
						if (err) {
							mongodb.close();
							return callback(err);
						}

						collection.findOne({"name" : name}, function(err, doc) {
							if (doc) {
								res.writeHead(200, {
									'Content-Length': doc.file.length,
									'Content-Type': 'multipart' });
								res.write(doc.file,'binary');
								res.end();
								fs.writeFile(filePath, doc.file, 'binary', function(err){
									if(err){
										throw err;
									}
								});
							}
							mongodb.close();
						});
					});
				});
			});
		}
	});
	//res.sendfile(filePath);
});

router.post('/post',function(req,res){
	console.log(req.body);
	console.log(req);
});

router.get('/test',function(req,res){
	var rs=fs.createReadStream('/home/gxcissad/nodejs/e-homeland/public/cache/2.jpg');

	rs.on('data',function(data){
		res.write(data);
	});
});

router.get('/backstage',checkSession);
router.get('/backstage',function(req,res){
	var username=req.session.admin.name;
	var password=req.session.admin.password;
	var ip=req.connection.remoteAddress;

	Admin.get(username, function(err, admin) {
		if (!admin) {
			req.flash('error', '用户不存在');
			//return res.redirect('/');
			console.log("[ERROR] ip "+ip+" is trying to login with invalid id : "+ username+" . "+new Date());
			return res.redirect('/');
		}
		if (admin.password != password) {
			req.flash('error', '用户名或密码错误');
			console.log("[ERROR] ip "+ip+" is trying to login the account : "+ username+"  with the wrong password. "+new Date());
			return res.redirect('/');
		}
		if(admin.ip!=ip){
			req.flash('error','你的帐号已在别处登录');
			console.log("[ERROR] user "+username+" is kicked out by "+admin.ip+" . "+new Date());
			return res.redirect('/');
		}

		console.log("[TASK] user "+admin.name+" login success . "+new Date());
		req.flash('success', req.session.admin.name + '登录成功');

		res.render('backstage', { title: 'E-backstage' ,user : username ,"layout":false,"part":[  
			{"name":"gainover","address":"test1","phone":"133"},  
			{"name":"zongzi","address":"test2","phone":"186"},  
			{"name":"maomao","address":"test3","phone":"150"}  
			]});
	});
});

router.get('/xmlControlCenter',function(req,res){
	console.log(req.query);
	var json={"content":[
	{"name":"gainover","address":"test1","phone":"133"},  
	{"name":"zongzi","address":"test2","phone":"186"},  
	{"name":"maomao","address":"test3","phone":"150"}  	
	]};
	res.send(json);
	//res.render('xmlControlCenter', { title: 'E-backstage' ,"layout":false,"part":[  
	//	        {"name":"gainover","address":"test1","phone":"133"},  
	//	        {"name":"zongzi","address":"test2","phone":"186"},  
	//	        {"name":"maomao","address":"test3","phone":"150"}  
	//]});
});

router.post('/jsonreceipt',function(req,res){
	console.log(req.body);
	console.log(req.files);
	res.send("done");
});

router.get('/login',function(req,res){
	res.render('login', { title: 'E-homeland'});
});

router.post('/logout',function(req,res){
	req.session.destroy();
});

router.post('/login-pass',function(req,res){
	var username=req.body['id'];
	var password=req.body['pwd'];
	var ip=req.connection.remoteAddress;

	Admin.get(username, function(err, admin) {
		if (!admin) {
			req.flash('error', '用户不存在');
			console.log("[ERROR] ip "+ip+" is trying to login with invalid id : "+ username+" . "+new Date());
			return res.redirect('/');
		}
		if (admin.password != password) {
			req.flash('error', '用户名或密码错误');
			console.log("[ERROR] ip "+ip+" is trying to login the account : "+ username+"  with the wrong password. "+new Date());
			return res.redirect('/');
		}
		Admin.update(admin.name,ip,function(err){
			console.log("[TASK] user "+admin.name+" ip update done . "+new Date());
		});
		admin.ip=ip;
		console.log("[TASK] user "+admin.name+"  login success . "+new Date());
		req.session.admin = admin;
		req.flash('success', req.session.admin.name + '登录成功');

		return res.redirect('/backstage');
	});
});

/*
router.post("/reg", function(req, res) {
	console.log(req.body['username']);
	console.log(req.body['pwd']);

	var md5 = crypto.createHash('md5');
	var password = md5.update(req.body.username+req.body.pwd).digest('base64');
	console.log(password);

	var newUser = new User({
		name: req.body.username,
		password: password,
	});
	//检查用户名是否已经存在
	User.get(newUser.name, function(err, user) {
		if (user) {
			err = 'Username already exists.';
		}
		if (err) {
			res.send('error');
			return res.redirect('/reg');
		}

		newUser.save(function(err) {
			if (err) {
				res.send('error');
				return res.redirect('/reg');
			}
			req.session.user = newUser;
			res.send('success', req.session.user.name+'注册成功');
			//res.redirect('/');
		});
	});
});
*/

module.exports = router;

function checkSession(req,res,next){
	if (req.session.admin) {
		next();
	}else{
		return res.redirect('/');
	}
};
