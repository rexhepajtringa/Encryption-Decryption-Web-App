import React, { useState, useRef, useEffect } from 'react';
import { Box, Heading, FormControl, FormLabel, Input, Select, Button, Flex } from '@chakra-ui/react';
import DSAVerify from './DSAVerify';
import DSASign from './DSASign';
import { generateDSAKey, getFilteredAliases, getDSAPrivateKey, getDSAPublicKey } from '../services/api';
import { useAuth } from '../context/AuthContext';

const DSA = () => {
    const { token, name } = useAuth();
    const [keyAlias, setKeyAlias] = useState('');
    const [keySize, setKeySize] = useState('');
    const [randomnessSource, setRandomnessSource] = useState('');
    const [privateKey, setPrivateKey] = useState('');
    const [publicKey, setPublicKey] = useState('');
    const [selectedKey, setSelectedKey] = useState('');
    const [keys, setKeys] = useState([]);

    const privateDivRef = useRef(null);
    const publicDivRef = useRef(null);

    const autoResizeDiv = (ref) => {
        if (ref.current) {
            ref.current.style.height = 'auto';
            ref.current.style.height = ref.current.scrollHeight + 'px';
        }
    };

    useEffect(() => {
        autoResizeDiv(privateDivRef);
        autoResizeDiv(publicDivRef);
    }, [privateKey, publicKey]);

    useEffect(() => {
        getFilteredAliases(token, "dsa_", name).then((keys) => {
            setKeys(keys);
        });
    }, [token]);

    const handleGenerateKeys = () => {
        // Logic to generate the keys based on input values
        // For demonstration purposes, simply setting random strings as the keys
        const thisKeyAlias = "dsa_" + keyAlias;
        generateDSAKey(token, keySize, keyAlias, randomnessSource, name).then((key) => {
            setSelectedKey(thisKeyAlias);
            setKeys([thisKeyAlias, ...keys]);
            getDSAPrivateKey(token, thisKeyAlias, name).then((key) => {
                setPrivateKey(key);
            });
            getDSAPublicKey(thisKeyAlias).then((key) => {
                setPublicKey(key);
            });
        });
    };

    const handleSelectKey = (key) => {
        setSelectedKey(key);
        getDSAPrivateKey(token, key, name).then((key) => {
            setPrivateKey(key);
        });
        getDSAPublicKey(key).then((key) => {
            setPublicKey(key);
        });
    };

    return (
        <>
            <Box p={8} textAlign="center">
                <Heading as="h1" mb={6}>DSA Encryption</Heading>
                <Box width="80%" margin="0 auto">
                    <Flex justifyContent="space-between">
                        <Box width="48%">
                            <form>
                                <FormControl id="keyAlias" mb={4}>
                                    <FormLabel>Key Name</FormLabel>
                                    <Input
                                        type="text"
                                        placeholder='Enter the key name...'
                                        value={keyAlias}
                                        onChange={(e) => setKeyAlias(e.target.value)} />
                                </FormControl>
                                <FormControl id="keySize" mb={4}>
                                    <FormLabel>Key Size</FormLabel>
                                    <Select value={keySize} onChange={(e) => setKeySize(e.target.value)}>
                                        <option value="">Select key size</option>
                                        <option value="512">512 bits</option>
                                        <option value="1024">1024 bits</option>
                                        <option value="2048">2048 bits</option>
                                    </Select>
                                </FormControl>
                                <FormControl id="randomnessSource" mb={4}>
                                    <FormLabel>Randomness Source</FormLabel>
                                    <Select value={randomnessSource} onChange={(e) => setRandomnessSource(e.target.value)}>
                                        <option value="">Select randomness</option>
                                        <option value="DRBG">DRBG</option>
                                        <option value="WINDOWS-PRNG">WINDOWS-PRNG</option>
                                        <option value="SHA1PRNG">SHA1PRNG</option>
                                    </Select>
                                </FormControl>
                                <Button colorScheme="teal" onClick={handleGenerateKeys} mb={4} mt={4}>Generate Keys</Button>
                            </form>

                        </Box>
                        <Box width="48%">
                            <FormControl mb={4}>
                                <FormLabel>Select Key</FormLabel>
                                <Select value={selectedKey} onChange={(e) => handleSelectKey(e.target.value)}>
                                    <option value="">No keys available</option>
                                    {keys.map((key) => (
                                        <option key={key} value={key}>{key}</option>
                                    ))}
                                </Select>
                            </FormControl>
                            <FormControl>
                                <FormLabel>Private Key</FormLabel>
                                <div
                                    ref={privateDivRef}
                                    contentEditable={false}
                                    style={{
                                        border: '1px solid #CBD5E0',
                                        borderRadius: '0.25rem',
                                        padding: '0.5rem',
                                        minHeight: '7rem',
                                        maxHeight: '10rem',
                                        overflow: 'auto',
                                        resize: 'none',
                                        width: '100%',
                                        color: privateKey ? '#000' : '#CBD5E0',
                                        marginBottom: '1rem'
                                    }}
                                >
                                    {privateKey ? privateKey : 'View the private key here...'}
                                </div>
                                <FormLabel>Public Key</FormLabel>
                                <div
                                    ref={publicDivRef}
                                    contentEditable={false}
                                    style={{
                                        border: '1px solid #CBD5E0',
                                        borderRadius: '0.25rem',
                                        padding: '0.5rem',
                                        minHeight: '7rem',
                                        maxHeight: '10rem',
                                        overflow: 'auto',
                                        resize: 'none',
                                        width: '100%',
                                        color: publicKey ? '#000' : '#CBD5E0'
                                    }}
                                >
                                    {publicKey ? publicKey : 'View the public key here...'}
                                </div>
                            </FormControl>
                        </Box>
                    </Flex>
                </Box>
            </Box>
            <DSASign keys={keys} />
            <DSAVerify keys={keys}/>
        </>
    );
};

export default DSA;
