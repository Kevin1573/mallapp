CREATE TABLE payment_merchants (
  id VARCHAR(32) PRIMARY KEY,
  app_id VARCHAR(64) NOT NULL,
  mch_id VARCHAR(32) NOT NULL,
  api_v3_key VARCHAR(64) NOT NULL,
  cert_serial VARCHAR(64) NOT NULL,
  private_key TEXT NOT NULL,
  status TINYINT DEFAULT 1
);

CREATE TABLE payment_orders (
  order_id VARCHAR(64) PRIMARY KEY,
  merchant_id VARCHAR(32) NOT NULL,
  amount INT NOT NULL,
  status VARCHAR(20),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
