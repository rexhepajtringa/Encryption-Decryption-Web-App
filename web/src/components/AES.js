import React, { useState, useRef, useEffect } from 'react';
import { Box, Heading, FormControl, FormLabel, Input, Select, Button, Flex } from '@chakra-ui/react';
import Cryptor from './AESCryptor';
import { useAuth } from '../context/AuthContext';
import { generateAESKey, getAESKey, getFilteredAliases } from '../services/api';

const AES = () => {
    const {token, setToken, name} = useAuth();
    const [keyAlias, setKeyAlias] = useState('');
    const [keySize, setKeySize] = useState('');
    const [randomnessSource, setRandomnessSource] = useState('');
    const [generatedKey, setGeneratedKey] = useState('');
    const [selectedKey, setSelectedKey] = useState('');
    const [keys, setKeys] = useState([]);

    // Ref for the div element
    const divRef = useRef(null);

    // Function to dynamically resize the div to fit its content
    const autoResizeDiv = () => {
        if (divRef.current) {
            divRef.current.style.height = 'auto';
            divRef.current.style.height = divRef.current.scrollHeight + 'px';
        }
    };

    // Call autoResizeDiv function whenever the generatedKey changes
    useEffect(() => {
        autoResizeDiv();
        getFilteredAliases(token, "aes_", name).then((keys) => {
            setKeys(keys);
        });
    }, []);

    const resetState = () => {
        setKeyAlias('');
        setKeySize('');
        setRandomnessSource('');
        setGeneratedKey('');
    }

    const handleGenerateKey = () => {
        // const randomKey = Math.random().toString(36).substr(2, 8);
        // setGeneratedKey(randomKey);
        generateAESKey(token, keySize, keyAlias, randomnessSource ,name).then((key) => {
            resetState();
            setGeneratedKey(key);
            setKeys([...keys, "aes_"+keyAlias]);
            setSelectedKey("aes_"+keyAlias);
        })


    };

    const handleSelectKey = (key) => {
        if(key){
            console.log(key)
            getAESKey(token, key, name).then((key) => {
                setGeneratedKey(key);
            });
        }
        setSelectedKey(key);
        // Logic to handle the selected key
    };

    return (
        <>
            <Box p={8} textAlign="center">
                <Heading as="h1" mb={6}>AES Encryption</Heading>
                <Box width="80%" margin="0 auto">
                    <Flex justifyContent="space-between">
                        <Box width="48%">
                            <form>
                                <FormControl id="keyAlias" mb={4}>
                                    <FormLabel>Key Name</FormLabel>
                                    <Input type="text" placeholder='Enter key name...' value={keyAlias} onChange={(e) => setKeyAlias(e.target.value)} />
                                </FormControl>
                                <FormControl id="keySize" mb={4}>
                                    <FormLabel>Key Size</FormLabel>
                                    <Select value={keySize} onChange={(e) => setKeySize(e.target.value)}>
                                        <option value="">Select size</option>
                                        <option value="128">128 bits</option>
                                        <option value="192">192 bits</option>
                                        <option value="256">256 bits</option>
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
                                <Button colorScheme="teal" onClick={handleGenerateKey} mb={4} mt={4}>Generate Key</Button>
                            </form>
                        </Box>
                        <Box width="48%">
                            <FormControl mb={4}>
                                <FormLabel>Select Key</FormLabel>
                                <Select value={selectedKey} onChange={(e) => handleSelectKey(e.target.value)}>
                                    <option value="">Select key</option>
                                    {keys.map((key) => (
                                        <option key={key} value={key}>{key}</option>
                                    ))}
                                </Select>
                            </FormControl>
                            <div
                                ref={divRef}
                                contentEditable={false} // Disable content editing
                                style={{
                                    border: '1px solid #CBD5E0', // Border color
                                    borderRadius: '0.25rem', // Border radius
                                    padding: '0.5rem', // Padding
                                    minHeight: '14.2rem', // Minimum height
                                    overflow: 'auto', // Enable scrolling if content overflows
                                    resize: 'none', // Disable resizing
                                    width: '100%', // Full width
                                    color: generatedKey ? '#000' : '#CBD5E0', // Text color
                                }}
                            >
                                {generatedKey ? generatedKey : 'View the key here...'}
                            </div>
                        </Box>
                    </Flex>
                </Box>
            </Box>
            <Cryptor keys={keys} setKeys={setKeys}/>
        </>
    );
};

export default AES;
