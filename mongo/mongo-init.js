db.auth('admin', 'admin');

db = db.getSiblingDB('b2b');

db.createUser({
  user: 'user',
  pwd: 'password',
  roles: [
    {
      role: 'readWrite',
      db: 'b2b',
    },
  ],
});