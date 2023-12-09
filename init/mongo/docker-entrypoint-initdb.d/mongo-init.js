// log the start if the script execution
print('start');

// Switch to the 'product-service' database (or create it if it doesn't exist)
db = db.getSiblingDB('product-service');

db.createUser(
    {
        user: 'rootadmin',
        pwd: 'password',
        roles: [{ role:'readWrite', db:'product-service' }]
    }
);

// Create a new collection named 'user' within the 'product-service' database
db.createCollection('user');

// Log the end of the script execution
print('END')



