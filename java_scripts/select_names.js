(function () {
  const asdPath = '//*[@id="all_words"]/div/div/ul/li/a';

  const res = document.evaluate(asdPath, document, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);

  const names = [];

  while (true) {
    const s = res.iterateNext();
    if (!s) break;
    names.push(s.innerHTML);
  }

  // console.log(names);

  const savedStr = localStorage["names"];

  const savedArray = [];
  if (savedStr && savedStr.length > 0) {
    savedStr.split(":").forEach(s => savedArray.push(s));
  }

  names.forEach(s => savedArray.push(s));

  const map = {};
  savedArray.forEach(s => map[s] = 1);

  const cleanArray = [];
  for (const s in map) {
    cleanArray.push(s);
  }

  localStorage["names"] = cleanArray.join(":");

  console.log(cleanArray);
  console.log(cleanArray.length);

  const linkPath = '//*[@id="dictionary-list"]/nav/div/div/a[@rel="“next”"]';
  const aRef = document.evaluate(linkPath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
  aRef.click();

})();