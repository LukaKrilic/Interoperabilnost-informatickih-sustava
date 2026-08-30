-- Faza 8: uredno pocetno stanje za obranu.
--
-- Tijekom razvoja je tablica narasla na devetak redaka s duplikatima, iz dva razloga:
--   1. samples/tags-valid.json nema gid, pa svaki ponovni uvoz doda jos dva retka
--   2. primjer u graphql.http je mutacijom preimenovao postojecu oznaku
-- Oba su ocekivano ponasanje, ali za demo zelimo cist popis.
--
-- Oznake se namjerno seedaju BEZ gid-a: gid je Asanin identifikator, a ove su
-- lokalno stvorene. Time i samples/tags-valid.xml (koji nosi gid 1200000000000001)
-- moze proci uvoz jednom, a drugi put uredno vraca 409.

DELETE FROM tag;

INSERT INTO tag (gid, name, color, notes, workspace_gid) VALUES
    (NULL, 'urgent',   'hot-pink', 'Hitni zadaci',       NULL),
    (NULL, 'backend',  'blue',     NULL,                 NULL),
    (NULL, 'frontend', 'magenta',  NULL,                 NULL),
    (NULL, 'školski',  'red',      'Fakultetski zadaci', NULL);
