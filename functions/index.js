const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();
const db = admin.firestore();

// Scheduled function runs every minute (for testing)
exports.updateOrderStatus = functions.pubsub
  .schedule("every 1 minutes") // Change to 'every 5 minutes' or cron as needed
  .onRun(async (context) => {
    const now = Date.now();
    const ordersRef = db.collection("orders");

    const snapshot = await ordersRef.get();
    snapshot.forEach(async (doc) => {
      const order = doc.data();
      const orderTime = order.timestamp || now;
      let newStatus = order.status;

      // Example logic:
      const elapsedMinutes = (now - orderTime) / (1000 * 60);

      if (order.status === "Pending" && elapsedMinutes >= 1) { // 1 min for testing
        newStatus = "Shipped";
      } else if (order.status === "Shipped" && elapsedMinutes >= 2) { // 2 min for testing
        newStatus = "Delivered";
      }

      if (newStatus !== order.status) {
        await ordersRef.doc(doc.id).update({ status: newStatus });
        console.log(`Order ${doc.id} status updated to ${newStatus}`);
      }
    });

    return null;
  });
