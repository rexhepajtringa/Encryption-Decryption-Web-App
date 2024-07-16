import React, { useState, useRef, useEffect } from 'react';
import { Box, Heading, FormControl, FormLabel, Textarea, Select, Button, Flex, Radio, RadioGroup, Stack } from '@chakra-ui/react';
import { decryotRSA, encryptRSA, getFilteredAliases, getPublicKeys } from '../services/api';
import { useAuth } from '../context/AuthContext';

const EncryptDecryptForm = ({ keys }) => {
    const { token, name } = useAuth();
    const [plainText, setPlainText] = useState('');
    const [selectedKey, setSelectedKey] = useState('');
    const [result, setResult] = useState('');
    const [operation, setOperation] = useState('encrypt');
    const [allKeys, setAllKeys] = useState([]);
    const [currentKeys, setCurrentKeys] = useState([]);

    const divRef = useRef(null);

    const autoResizeDiv = () => {
        if (divRef.current) {
            divRef.current.style.height = 'auto';
            divRef.current.style.height = divRef.current.scrollHeight + 'px';
        }
    };

    useEffect(() => {
        autoResizeDiv();
    }, [result]);


    useEffect(() => {
        updateCurrentKeys();
        setSelectedKey(keys[0] || '');
    }, [keys, operation]);

    useEffect(() => {
        getPublicKeys(token, name).then((keys) => {
            setAllKeys(keys.map((key) => key.replace('_public', '')).filter((key) => key.startsWith('rsa_')));
            updateCurrentKeys();
        });
    }, [token]);

    const getAllKeys = () => {
        console.log("HINA", allKeys)
        const combined = [...keys, ...allKeys];
        const unique = [...new Set(combined)];
        console.log("HINA", unique)
        return unique;
    }

    const updateCurrentKeys = () => {
        if (operation === 'encrypt') {
            setCurrentKeys([...getAllKeys()]);
        } else {
            setCurrentKeys(keys);
        }
    };

    const handleEncryptDecrypt = () => {
        // Placeholder logic for encryption/decryption
        if (operation === 'encrypt') {
            encryptRSA(plainText, selectedKey).then((encryptedText) => {
                setResult(encryptedText);
            });
        }
        else {
            decryotRSA(token, plainText, selectedKey, name).then((decryptedText) => {
                setResult(decryptedText);
            });
        }
    };

    const handleFileUpload = (event) => {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                setPlainText(e.target.result);
            };
            reader.readAsText(file);
            setPlainText(file);
        }
    };

    const handleFileDownload = () => {
        const element = document.createElement("a");
        const file = new Blob([result], { type: 'text/plain' });
        element.href = URL.createObjectURL(file);
        element.download = "result.txt";
        document.body.appendChild(element);
        element.click();
    };

    return (
        <Box p={8} textAlign="center">
            <Heading as="h1" mb={6}>Encrypt/Decrypt Text</Heading>
            <Box width="80%" margin="0 auto">
                <Flex justifyContent="space-between">
                    <Box width="45%">
                        <FormControl id="plainText" mb={4}>
                            <FormLabel>Text</FormLabel>
                            <Textarea
                                value={plainText}
                                onChange={(e) => setPlainText(e.target.value)}
                                placeholder="Enter your text here..."
                                size="md"
                                resize="vertical"
                            />
                        </FormControl>
                        <Button
                            colorScheme="teal"
                            variant="outline"
                            as="label"
                            htmlFor="file-upload"
                            mt={2}
                        >
                            Upload Text File
                            <input
                                type="file"
                                id="file-upload"
                                style={{ display: "none" }}
                                accept=".txt"
                                onChange={handleFileUpload}
                            />
                        </Button>
                        <FormControl id="selectedKey" mb={4} mt={4}>
                            <FormLabel>Select Key</FormLabel>
                            <Select
                                value={selectedKey}
                                onChange={(e) => setSelectedKey(e.target.value)}
                            >
                                
                                {currentKeys.length > 0 ? (
                                    currentKeys.map((key) => (
                                        <option key={key} value={key}>{key}</option>
                                    ))
                                ) : (
                                    <option value="">No keys available</option>

                                )}
                            </Select>
                        </FormControl>
                        <Box mt={4}>
                            <RadioGroup onChange={setOperation} value={operation}>
                                <Flex direction="row" justifyContent="space-around">
                                    <Stack direction="row">
                                        <Radio value="encrypt">Encrypt</Radio>
                                        <Radio value="decrypt">Decrypt</Radio>
                                    </Stack>
                                    <Button colorScheme="teal" onClick={handleEncryptDecrypt}>
                                        Process
                                    </Button>
                                </Flex>
                            </RadioGroup>
                        </Box>
                    </Box>
                    <Box width="45%">
                        <FormControl mb={4}>
                            <FormLabel>Result</FormLabel>
                            <div
                                ref={divRef}
                                contentEditable={true} // Disable content editing
                                style={{
                                    border: '1px solid #CBD5E0', // Border color
                                    borderRadius: '0.25rem', // Border radius
                                    padding: '0.5rem', // Padding
                                    minHeight: '10rem', // Minimum height
                                    overflow: 'auto', // Enable scrolling if content overflows
                                    resize: 'none', // Disable resizing
                                    width: '100%', // Full width
                                    color: result ? '#000' : '#CBD5E0', // Text color
                                    textAlign: 'left' // Align text to the left
                                }}
                            >
                                {result ? result : 'View the result here...'}
                            </div>
                        </FormControl>
                        <Button
                            colorScheme="teal"
                            variant="outline"
                            onClick={handleFileDownload}
                            mt={2}
                        >
                            Download Result
                        </Button>
                    </Box>
                </Flex>
            </Box>
        </Box>
    );
};

export default EncryptDecryptForm;
