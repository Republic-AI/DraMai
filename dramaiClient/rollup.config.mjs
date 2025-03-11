import resolve from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';
import json from '@rollup/plugin-json';

export default {
  input: 'node_modules/@solana/web3.js/lib/index.browser.esm.js', // 输入文件路径
  output: {
    file: 'dist/web3.umd.min.js', // 输出文件路径
    format: 'umd', // 打包格式
    name: 'solanaWeb3', // 全局变量名
  },
  plugins: [resolve(), commonjs(),json()],
};