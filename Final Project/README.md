# Search Project - Initial Setup

## 1. MongoDB Setup.

<ol type ="a">
a. Download MongoDB Community Server from <a href = "https://www.mongodb.com/download-center#community" >here</a>.

b. Install MongoDB on your local machine.

c. Modify System environment variables [bin path of MongoDB]. 

d. Open command prompt[Administrator] and start mongodb server by passing **_mongod_** command to the command prompt.

e. Open an another instance of command prompt [Administrator]  and start a client by passing **_mongo_** to the command prompt.

f. If everything is properly setuped you will be connected to MongoDB Sever.

</ol>

**Note:** Make sure that environment variables are correct.If not every time you need to move to bin folder of MongoDb Server to perform any operation on MongoDB.

**Warning:** If there is a problem with **MongoDB** Database Path run the following command.You can use this command to change the database path and this is the place where the MongoDB Index will be stored.

```
mongod --dbpath [CustomFilePath]
``` 



## 2. Importing yelp data to MongoDB

<ol>
a. <a href="https://www.yelp.com/dataset/challenge">Download</a> the <strong>JSON dataset</strong> from yelp data challenge.

b. Extract tar file using WinRar or using command line **_tar -xvzf [tarFile] [path/filename]_**

c. Run the following command on your instance.
 
 ```
mongoimport --db yelp --collection business --drop --file D:\dataset\business.json
mongoimport --db yelp --collection checkin --drop --file D:\dataset\checkin.json
mongoimport --db yelp --collection photos --drop --file D:\dataset\photos.json
mongoimport --db yelp --collection review --drop --file D:\dataset\review.json
mongoimport --db yelp --collection tip --drop --file D:\dataset\tip.json
mongoimport --db yelp --collection user --drop --file D:\dataset\user.json
```

d. Now we have successfully imported 6 datasets to MongoDB.
```
Database = Yelp [business,checkin,photos,review,tip,user]
```

</ol>

## 3. Setting up Robo 3T to view the data.

To view the data in MongoDB we will be using Robo 3T. 
<ol>
a. Download Robo 3T from <a href="https://robomongo.org/download">here</a>.

b.Install Robo 3T on yoour local machine.

c. Connect to mongoDb passing the following parameters to initial setup when asked.

```
// Default Parameters.Please pass in custom values if you have modified any default parameters of mongoDB.

Name : Local 
Address : 127.0.0.1 
Port : 27017 
```

d. Now you can see all the databases and their associated Collections that are available in the local instance of Mongo DB.

e. Under collections you need to find all your **Six** collections.


</ol>

## 4. Jump Start : Sample Queries and Code.

a. To retrieve business Id of all the restaurants that are serving Indian Food.Try to run it in **Robo 3T**.

```
db.getCollection('business').find({"categories" : {$elemMatch : { $eq : "Indian"}}},{business_id :1,_id:0})
```

b. Sample Python Code for connecting to MongoDb Instance using PyMongo Driver.

```
# Install pymongo driver using pip.

from pymongo.mongo_client import MongoClient

class ClientConnection:
    def __init__(self):
        self.client = MongoClient('mongodb://localhost:27017')
        self.db = self.client.yelp
        self.col_tip = self.db['user'] # collection : use
        self.col_review = self.db['tip'] # collection : tip
        self.col_photos = self.db['photos'] # collection : photos
        self.col_user = self.db['checkin'] # collection : checkin
        self.col_bus = self.db['business'] # collection : business

    def test_find(self):
        print("Testing Tip")
        print(self.col_tip.find_one())
        print("Testing User Data")
        print(self.col_review.find_one())
        print("Testing Photos Data")
        print(self.col_photos.find_one())
        print("Testing Business Data")
        print(self.col_bus.find_one())


if __name__ == "__main__":
    client = ClientConnection()
    client.test_find()


```
