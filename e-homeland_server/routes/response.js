var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function(req, res) {
  //res.send(200,req.body.address);
 res.render('response', { title: 'test' });
});

module.exports = router;
