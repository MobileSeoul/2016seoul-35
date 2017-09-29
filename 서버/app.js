var express = require('express');
var path = require('path');

var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var routes = require('./routes/index');
var join = require('./routes/join');
var mboard = require('./routes/mboard');
var del = require('./routes/delete');
var mup = require('./routes/update');
var img = require('./routes/img');
var review = require('./routes/review');
var login = require('./routes/login');
var facil = require('./routes/facil');
var del_facil = require('./routes/del_facil');
var up_facil = require('./routes/up_facil');

var comm = require('./routes/comm');
var reply = require('./routes/reply')
var review = require('./routes/review');
var like = require('./routes/like');

/******************Travel Planner*********************/
var users = require('./routes/users');
var join_travel = require('./routes/join_travel');
var plan = require('./routes/plan');
var login_travel = require('./routes/login_travel');
var place = require('./routes/place');
/******************Travel Planner*********************/

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', routes);
app.use('/join', join);
app.use('/mboard', mboard);
app.use('/mup', mup);
app.use('/img', img);
app.use('/login',login);

app.use('/facil', facil);
app.use('/del_facil',del_facil);
app.use('/up_facil',up_facil);

app.use('/review',review);
app.use('/comm',comm);
app.use('/reply',reply);
app.use('/delete', del);
app.use('/like', like);

/******************Travel Planner*********************/
app.use('/users', users);
app.use('/join_travel', join_travel);
app.use('/plan', plan);
app.use('/login_travel', login_travel);
app.use('/place', place);
/******************Travel Planner*********************/



// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

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
