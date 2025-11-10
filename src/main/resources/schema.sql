CREATE TABLE payment_transactions
(
    id         UUID PRIMARY KEY,
    tenant_id  VARCHAR(255)   NOT NULL,
    dealer_id  UUID           NOT NULL,
    amount     NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    method     VARCHAR(20)    NOT NULL CHECK (method IN ('UPI', 'CARD', 'NET_BANKING')),
    status     VARCHAR(20)    NOT NULL CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')),
    request_id VARCHAR(255)   NOT NULL UNIQUE,
    created_at TIMESTAMP      NOT NULL,
    updated_at TIMESTAMP      NOT NULL
);

CREATE INDEX idx_payment_tenant_id ON payment_transactions (tenant_id);
CREATE INDEX idx_payment_dealer_id ON payment_transactions (dealer_id);
CREATE INDEX idx_payment_request_id ON payment_transactions (request_id);
CREATE INDEX idx_payment_status ON payment_transactions (status);
CREATE INDEX idx_payment_tenant_dealer ON payment_transactions (tenant_id, dealer_id);
CREATE INDEX idx_payment_status_created ON payment_transactions (status, created_at);

CREATE INDEX idx_payment_pending_processing ON payment_transactions (status, created_at)
    WHERE status = 'PENDING';

