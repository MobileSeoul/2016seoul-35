var express = require('express');
var mysql = require('mysql');
var router = express.Router();
var connection = mysql.createConnection({
  'host' : 'aws-rds-mysql.cjj4qeglpmie.us-west-2.rds.amazonaws.com',
  'user' : 'user',
  'password' : 'rlawlsrb1!',
  'database' : 'TravelPlanner',
});

/*
 * Method       : POST
 * Path         : http://52.34.206.80:3000/login
 * Description  : 로그인을 합니다.
 */
router.post('/', function(req, res, next) { 
  // debug
  console.log(req.body);
  
  // ID와 PW를 DB와 비교하여 같다면 로그인을합니다.
  connection.query('select * from Member where id=?;', [req.body.id], function (error, cursor) {
    if (error == null) {
      if (cursor.length == 1) {
        // DB에 등록되어 있는 PW와 사용자가 입력한 PW 비교
        if (req.body.pw == cursor[0].pw) {
          res.status(200).json({ result : true });
        } else {
          // debug
          // console.log("요청 pw : " + req.body.pw);
          // console.log("DB pw : " + cursor.pw);
          res.status(406).json({ result : false, message : 'This pw is not correct'});
        }
      } else {
        res.status(405).json({ result : false, message : 'This ID is not exist' });
      }
    } else {
      // debug
      // console.log('server DB load error');
      // res.status(503).json({ result : false, message : 'server DB error' })
    }
  });
});

module.exports = router;