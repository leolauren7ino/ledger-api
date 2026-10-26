ALTER TABLE contas
    ADD COLUMN usuario_id BIGINT NOT NULL REFERENCES usuarios (id);

CREATE INDEX idx_contas_usuario_id ON contas (usuario_id);

CREATE TABLE transferencias (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    conta_origem_id BIGINT NOT NULL REFERENCES contas (id),
    conta_destino_id BIGINT NOT NULL REFERENCES contas (id),
    valor NUMERIC(19,2) NOT NULL CHECK (valor > 0),
    criada_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (conta_origem_id <> conta_destino_id)
);

CREATE TABLE lancamentos (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    conta_id BIGINT NOT NULL REFERENCES contas (id),
    transferencia_id BIGINT REFERENCES transferencias (id),
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('DEPOSITO', 'DEBITO', 'CREDITO')),
    valor NUMERIC(19,2) NOT NULL CHECK (valor > 0),
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_lancamentos_conta_id ON lancamentos (conta_id, criado_em);