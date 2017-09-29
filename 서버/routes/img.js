var express = require('express');
var mysql = require('mysql');
var fs = require('fs');
var bodyParser = require('body-parser');
var http = require('http');

var router = express.Router();

var server = http.createServer(router);

router.get('/', function(req, res) {
    res.render('img', function(error, content) {
        if(!error) {
            res.end(content);
        } else {
            res.writeHead(501, { 'Content-Type' : 'text/plain' });
            res.end('Error while reading a file');
        }
    });
});

router.get('/imgs',function(req, res){
    fs.readFile('test1.jpg', function(error,data){
        res.writeHead(200,{'Content-Type': 'text/html'});
        res.end(data);
    });
});
/*
router.get('/:name',function(req, res){
    var filename = req.params.name;
    console.log(__dirname+'/img/'+filename);
    fs.exists(__dirname+'/img/'+filename, function (exists){
        if(exists){
            fs.readFile(__dirname+'/img/'+filename, fucntion(err,data){
                        res.end(data);
                        });
        }else{
              res.ends('file is not exists');
              }
    });
   
});*/

module.exports=router;
/*
var fs = require('fs');
var express = require('express');
var app = express();

var http = require('http');
var server = http.createServer(app);

app.get('/img/:name',function(req, res){
    var filename = req.params.name;
    console.log(__dirname+'/img/'+filename);
    fs.exists(__dirname+'/img/'+filename, function (exists){
        if(exists){
            fs.readFile(__dirname+'/img/'+filename, fucntion(err,data){
                        res.end(data);
                        });
        }else{
              res.ends('file is not exists');
              }
    })
   
});
module.exports=router;*/
/*var express = require('express');
var fs = require('fs');
var app = express();

app.get('/imageup',function(req,res){
    fs.readFile('img.ejs',function(error,data){
        res.writeHead(200,{'Content-Type': 'text/html'});
        res.end(data);
    });
});
app.get('/imgs',function(req,res){
    fs.readFile('../img/11.jpg',function(error,data){
        res.writeHead(200,{'Content-Type':'text/html'});
        res.end(data);
    });
});
app.listen(3303,function(){
    console.log('Server Start.');
});

module.exports=app;*/
