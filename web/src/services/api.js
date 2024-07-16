const BACKEND_URL =
    process.env.REACT_APP_BACKEND_URL || "http://localhost:8090/api/crypto/";

// returns a Promise
function transformToJsonOrTextPromise(response) {
    const contentLength = response.headers.get("Content-Length");
    const contentType = response.headers.get("Content-Type");
    if (
        contentLength !== "0" &&
        contentType &&
        contentType.includes("application/json")
    ) {
        return response.json();
    } else {
        return response.text();
    }
}

async function sendRequest(url, { method = "GET", body, headers = {} }) {
    const options = {
        method,
        headers: new Headers({ "content-type": "application/json", ...headers }),
        body: body ? JSON.stringify(body) : null,
    };

    console.log(headers);

    return fetch(url, options).then((res) => {
        const jsonOrTextPromise = transformToJsonOrTextPromise(res);

        if (res.ok) {
            return jsonOrTextPromise;
        } else {
            return jsonOrTextPromise.then(function (response) {
                const responseObject = {
                    status: res.status,
                    ok: false,
                    message: typeof response === "string" ? response : response.message,
                };

                return Promise.reject(responseObject);
            });
        }
    });
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
export async function createKeyStore(password, name) {
    return sendRequest(BACKEND_URL + `create-keystore`, {
        method: "POST",
        body: {
            password,
            name,
        },
    });
}

export async function loginKeyStore(password, name) {
    return sendRequest(BACKEND_URL + `login-keystore`, {
        method: "POST",
        body: {
            password,
            name,
        },
    });
}

export async function generateAESKey(password, keySize, alias, randomAlgorithm, keystoreName) {
    // if(!randomAlgorithm) randomAlgorithm = undefined;
    console.log(password, keySize, alias, randomAlgorithm, keystoreName)
    return sendRequest(BACKEND_URL + `generate/aes`, {
        method: "POST",
        body: {
            keySize,
            alias,
            password,
            randomAlgorithm,
            keystoreName,
        },
    });
}

export async function getAESKey(password, alias, keystoreName) {
    const url = new URL(BACKEND_URL + `load/aes`);
    url.search = new URLSearchParams({ alias, password, keystoreName }).toString();
    return sendRequest(url, {});
}


export async function encryptAES(password, plainText, alias, keystoreName) {
    return sendRequest(BACKEND_URL + `encrypt/aes`, {
        method: "POST",
        body: {
            plainText,
            alias,
            password,
            keystoreName,
        },
    });
}


export async function decryotAES(password, cipherText, alias, keystoreName) {
    return sendRequest(BACKEND_URL + `decrypt/aes`, {
        method: "POST",
        body: {
            cipherText,
            alias,
            password,
            keystoreName,
        },
    });
}

export async function getAliases(password, keystoreName) {
    return sendRequest(BACKEND_URL + `aliases`, {
        headers: {
            password,
            keystoreName,
        },
    });
}


export async function generateRSAKey(password, keySize, alias, randomAlgorithm, keystoreName) {
    if(!randomAlgorithm) randomAlgorithm = undefined;
    return sendRequest(BACKEND_URL + `generate/rsa`, {
        method: "POST",
        body: {
            keySize,
            alias,
            password,
            randomAlgorithm,
            keystoreName,
        },
    });
}

export async function encryptRSA(plainText, alias) {
    return sendRequest(BACKEND_URL + `encrypt/rsa`, {
        method: "POST",
        body: {
            plainText,
            alias,
            // password,
        },
    });
}

export async function decryotRSA(password, cipherText, alias, keystoreName) {
    return sendRequest(BACKEND_URL + `decrypt/rsa`, {
        method: "POST",
        body: {
            cipherText,
            alias,
            password,
            keystoreName,
        },
    });
}


export async function generateDSAKey(password, keySize, alias, randomAlgorithm, keystoreName) {
    // if(!randomAlgorithm) randomAlgorithm = undefined;
    console.log(password, keySize, alias, randomAlgorithm, keystoreName)
    return sendRequest(BACKEND_URL + `generate/dsa`, {
        method: "POST",
        body: {
            keySize,
            alias,
            password,
            randomAlgorithm,
            keystoreName,
        },
    });
}


export async function singText(password, plainText, alias, keystoreName) {
    return sendRequest(BACKEND_URL + `sign-text`, {
        method: "POST",
        body: {
            plainText,
            alias,
            password,
            keystoreName,
        },
    });
}

export async function verifySignature(alias, text, signature) {
    return sendRequest(BACKEND_URL + `verify-text`, {
        method: "POST",
        body: {
            alias,
            // password,
            text,
            signature,
        },
    });
}

export async function getDSAPrivateKey(password, alias, keystoreName) {
    const url = new URL(BACKEND_URL + `dsa/private`);
    url.search = new URLSearchParams({ alias, password, keystoreName }).toString();
    return sendRequest(url, {});
}

export async function getDSAPublicKey(alias) {
    const url = new URL(BACKEND_URL + `dsa/public`);
    url.search = new URLSearchParams({ alias}).toString();
    return sendRequest(url, {});
}

export async function getRSAPublicKey(alias) {
    console.log("PUBLIKI", alias)
    const url = new URL(BACKEND_URL + `rsa/public`);
    url.search = new URLSearchParams({ alias}).toString();
    return sendRequest(url, {});
}

export async function getRSAPrivateKey(password, alias, keystoreName) {
    const url = new URL(BACKEND_URL + `rsa/private`);
    url.search = new URLSearchParams({ alias, password, keystoreName}).toString();
    return sendRequest(url, {});
}

export async function getFilteredAliases(password, filter, keystoreName) {
    return sendRequest(BACKEND_URL + `filter-aliases`, {
        method: "POST",
        body: {
            password,
            filter,
            keystoreName
        },
    });
}

export async function getPublicKeys() {
    return sendRequest(BACKEND_URL + `public-keys`, {});
}