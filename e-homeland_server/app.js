var express = require('express');
var cookieSession = require('cookie-session');
var mongoose = require('mongoose');
var session = require('express-session');
var path = require('path');
var favicon = require('static-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var connect = require('connect'); 
var debug = require('debug')('nodejs');
var MongoStore = require('connect-mongo')(session);
var methodOverride = require('method-override')
var fs = require('fs');
var multer  = require('multer')
var flash = require('express-flash');
var partials = require('express-partials');
var settings = require('./settings');
var routes = require('./routes/index');
var admin = require('./routes/admin');
var response=require('./routes/response');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');
app.use(partials());

app.use(favicon());
app.use(logger('dev'));

app.use(bodyParser.json());
app.use(bodyParser.urlencoded());
app.use(methodOverride());
app.use(cookieParser());
app.use(flash());
app.use(express.static(path.join(__dirname, 'public')));
app.use(multer({ dest: './public/uploads/'}))
app.use(session({
    secret:settings.cookieSecret,
    store:new MongoStore({
        db:settings.db,
    })
}));


app.use('/', routes);
//app.use('/*',routes);
app.use('/admin', admin);
app.use('/response',response);
//app.use('/reg',routes);

/// catch 404 and forward to error handler
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

/// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
        message: err.message,
        error: {}
    });
});


module.exports = app;