<script setup lang="ts">
import { onLaunch } from '@dcloudio/uni-app'

onLaunch(() => {
  console.log('融信贷启动')
})
</script>

<style>
page {
  min-height: 100%;
  color: #172421;
  background: #dfe8e4;
  font-family:
    "PingFang SC", "Microsoft YaHei", "Noto Sans CJK SC", sans-serif;
  font-synthesis: none;
  -webkit-font-smoothing: antialiased;
}

view,
text,
input,
button {
  box-sizing: border-box;
}

button {
  margin: 0;
  padding: 0;
  line-height: normal;
}

button::after {
  border: 0;
}

.input-placeholder {
  color: #a2ada9;
}
</style>
