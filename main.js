// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');

/**
 * Don't hard-code your credentials! Load them from disk or your environment
 * instead.
 */
AWS.config.update({accessKeyId: process.env.AWS_ACCESS_KEY, secretAccessKey: process.env.AWS_SECRET_KEY});
// Instead, do this:
// AWS.config.loadFromPath('./path/to/credentials.json');

// Set your region for future requests.
AWS.config.update({
	region : 'us-east-1'
});

// Create a bucket using bound parameters and put something in it.
// Make sure to change the bucket name from "myBucket" to something unique.
var s3bucket = new AWS.S3({
	params : {
		ACL : 'public-read',
		Bucket : 'fangorn-0987'
	}
});
s3bucket.createBucket(function() {
	var data = {
		Key : 'myKey',
		Body : 'Hello!'
	};
	s3bucket.putObject(data, function(err, data) {
		if (err) {
			console.log("Error uploading data: ", err);
		} else {
			console.log("Successfully uploaded data to myBucket/myKey");
		}
	});
});
